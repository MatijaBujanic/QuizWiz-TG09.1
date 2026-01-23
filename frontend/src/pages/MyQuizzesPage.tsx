import React, { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getEmailFromToken } from '../utils/authMapper';

interface Quiz {
  quiz_id: number;
  quiz_name: string;
  quiz_theme: string;
  description?: string;
  date: string;
  time: string;
  application_type: string;
  number_of_rounds: number;
  max_points: number;
  status: string;
  organizer_id?: number;
  location_id?: number;
}

interface Team {
  team_id: number;
  team_name: string;
  points: number;
  number_of_members: number;
}

interface TeamResult {
  teamId: number;
  points: number;
}

type Me = {
  user_id: number;
  email: string;
  username: string;
  role: string;
};

type RoleLookupResponse = {
  email: string;
  role: string;
  username: string;
  userId: number;
};

const MyQuizzesPage: React.FC = () => {
  const { token, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [me, setMe] = useState<Me | null>(null);
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedQuizForResults, setSelectedQuizForResults] = useState<Quiz | null>(null);
  const [teams, setTeams] = useState<Team[]>([]);
  const [teamPoints, setTeamPoints] = useState<Record<number, number>>({});
  const [submittingResults, setSubmittingResults] = useState(false);

  const axiosInstance = useMemo(
    () =>
      axios.create({
        baseURL: "http://quizwiz-tg091-production-504c.up.railway.app",
        withCredentials: true,
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      }),
    [token],
  );

  // 1) Load user via /api/users/role
  useEffect(() => {
    const loadMe = async () => {
      try {
        if (!isAuthenticated || !token) {
          setMe(null);
          return;
        }

        const email = getEmailFromToken(token);
        if (!email) {
          console.warn("Could not extract email from JWT token.");
          setMe(null);
          return;
        }

        const res = await axiosInstance.get<RoleLookupResponse>(
          "/api/users/role",
          {
            params: { email },
          },
        );

        const dto = res.data;
        const mapped: Me = {
          user_id: dto.userId,
          email: dto.email,
          username: dto.username,
          role: dto.role,
        };

        setMe(mapped);
      } catch (err) {
        console.error("Failed to load user via /api/users/role:", err);
        setMe(null);
      }
    };

    loadMe();
  }, [axiosInstance, isAuthenticated, token]);

  // Helper: reload quizzes
  const reloadQuizzes = async (organizerId: number, signal?: AbortSignal) => {
    const response = await axiosInstance.get('/api/quizzes', {
      signal: (signal as any) ?? undefined,
      params: { _ts: Date.now() },
      headers: {
        'Cache-Control': 'no-cache, no-store, must-revalidate',
        Pragma: 'no-cache',
        Expires: '0',
      },
    });
    const quizzesData = Array.isArray(response.data) ? response.data : (response.data.quizzes || []);
    // Privremeno: prikazuj i kvizove s organizer_id = null
    const organizerQuizzes = quizzesData.filter((q: Quiz) => {
      // Uključi kvizove koji nemaju organizera (null) ili imaju trenutnog organizera
      if (q.organizer_id === null || q.organizer_id === undefined) return true;
      return Number(q.organizer_id) === Number(organizerId);
    });
    setQuizzes(organizerQuizzes);
  };

  // 2) Load quizzes when user is loaded
  useEffect(() => {
    if (!isAuthenticated || !me?.user_id) return;

    const controller = new AbortController();

    (async () => {
      setLoading(true);
      setError(null);

      try {
        await reloadQuizzes(me.user_id, controller.signal);
      } catch (e: any) {
        if (e?.name === "CanceledError" || e?.name === "AbortError") return;
        console.error('Greška pri učitavanju kvizova:', e);
        setError(e?.response?.data?.message || e?.message || 'Greška pri učitavanju kvizova');
      } finally {
        setLoading(false);
      }
    })();

    return () => controller.abort();
  }, [isAuthenticated, me?.user_id, axiosInstance]);

  useEffect(() => {
    if (!isAuthenticated || !me?.user_id) return;

    const handleFocus = () => {
      reloadQuizzes(me.user_id).catch(() => undefined);
    };

    const handleVisibility = () => {
      if (document.visibilityState === 'visible') {
        reloadQuizzes(me.user_id).catch(() => undefined);
      }
    };

    window.addEventListener('focus', handleFocus);
    document.addEventListener('visibilitychange', handleVisibility);

    return () => {
      window.removeEventListener('focus', handleFocus);
      document.removeEventListener('visibilitychange', handleVisibility);
    };
  }, [isAuthenticated, me?.user_id]);

  const handleDelete = async (quizId: number) => {
    if (!window.confirm('Jeste li sigurni da želite obrisati ovaj kviz?')) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/organizer/quizzes/${quizId}`);
      alert('Kviz je uspješno obrisan!');
      
      // Reload quizzes to get fresh data
      if (me?.user_id) {
        await reloadQuizzes(me.user_id);
      }
    } catch (err: any) {
      console.error('Greška pri brisanju kviza:', err);
      alert(err.response?.data?.message || 'Greška pri brisanju kviza');
    }
  };

  const handleEdit = (quizId: number) => {
    navigate(`/edit-quiz/${quizId}`);
  };

  const handleManageResults = async (quiz: Quiz) => {
    setSelectedQuizForResults(quiz);
    try {
      const res = await axiosInstance.get(`/api/teams?quizId=${quiz.quiz_id}`);
      const teamsData = res.data.teams || [];
      setTeams(teamsData);
      
      const initialPoints: Record<number, number> = {};
      teamsData.forEach((team: Team) => {
        initialPoints[team.team_id] = team.points || 0;
      });
      setTeamPoints(initialPoints);
    } catch (err) {
      console.error('Greška pri učitavanju timova:', err);
      alert('Greška pri učitavanju timova');
    }
  };

  const handleCloseModal = () => {
    setSelectedQuizForResults(null);
    setTeams([]);
    setTeamPoints({});
  };

  const handleSubmitResults = async () => {
    if (!selectedQuizForResults) return;

    const results: TeamResult[] = teams.map(team => ({
      teamId: team.team_id,
      points: teamPoints[team.team_id] || 0
    }));

    setSubmittingResults(true);
    try {
      await axiosInstance.post(
        `/api/organizer/quizzes/${selectedQuizForResults.quiz_id}/results`,
        results
      );
      alert('Rezultati uspješno spremljeni!');
      handleCloseModal();
    } catch (err: any) {
      console.error('Greška pri slanju rezultata:', err);
      alert(err.response?.data?.message || 'Greška pri slanju rezultata');
    } finally {
      setSubmittingResults(false);
    }
  };

  if (loading) return <div className="container mt-4"><p>Učitavanje...</p></div>;
  if (error) return <div className="container mt-4"><div className="alert alert-danger">{error}</div></div>;

  return (
    <div className="container mt-4">
      <h1>Moji kvizovi</h1>
      {quizzes.length === 0 ? (
        <p className="mt-4">Nema kreiranih kvizova.</p>
      ) : (
        <div className="row mt-4">
          {quizzes.map((quiz) => (
            <div key={quiz.quiz_id} className="col-md-6 col-lg-4 mb-4">
              <div className="card h-100">
                <div className="card-body">
                  <h5 className="card-title">{quiz.quiz_name}</h5>
                  <p className="card-text small text-muted">{quiz.quiz_theme}</p>
                  {quiz.description && (
                    <p className="card-text">{quiz.description}</p>
                  )}
                  <div className="mb-3">
                    <p className="mb-1"><strong>Datum:</strong> {quiz.date}</p>
                    <p className="mb-1"><strong>Vrijeme:</strong> {quiz.time}</p>
                    <p className="mb-1"><strong>Tip:</strong> {quiz.application_type === 'team' ? 'Tim' : 'Pojedinac'}</p>
                    <p className="mb-1"><strong>Rundi:</strong> {quiz.number_of_rounds}</p>
                    <p className="mb-1"><strong>Max bodovi:</strong> {quiz.max_points}</p>
                    <p className="mb-1">
                      <strong>Status:</strong> 
                      <span className={`badge ms-2 ${
                        quiz.status === 'open' ? 'bg-success' :
                        quiz.status === 'closeds' ? 'bg-danger' :
                        'bg-warning'
                      }`}>
                        {quiz.status === 'open' ? 'Otvoren' : 
                         quiz.status === 'closed' ? 'Zatvoren' : quiz.status}
                      </span>
                    </p>
                  </div>
                </div>
                <div className="card-footer bg-white">
                  <button 
                    className="btn btn-sm btn-primary me-2"
                    onClick={() => handleEdit(quiz.quiz_id)}
                  >
                    Uredi
                  </button>
                  <button 
                    className="btn btn-sm btn-success me-2"
                    onClick={() => handleManageResults(quiz)}
                  >
                    Rezultati
                  </button>
                  <button 
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDelete(quiz.quiz_id)}
                  >
                    Obriši
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal za unos rezultata */}
      {selectedQuizForResults && (
        <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-lg modal-dialog-scrollable">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Unos rezultata - {selectedQuizForResults.quiz_name}</h5>
                <button type="button" className="btn-close" onClick={handleCloseModal}></button>
              </div>
              <div className="modal-body">
                {teams.length === 0 ? (
                  <p className="text-muted">Nema prijavljenih timova.</p>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-hover">
                      <thead>
                        <tr>
                          <th>Tim ID</th>
                          <th>Naziv tima</th>
                          <th>Članovi</th>
                          <th style={{ width: '150px' }}>Bodovi</th>
                        </tr>
                      </thead>
                      <tbody>
                        {teams.map((team) => (
                          <tr key={team.team_id}>
                            <td>{team.team_id}</td>
                            <td className="fw-semibold">{team.team_name}</td>
                            <td>{team.number_of_members}</td>
                            <td>
                              <input
                                type="number"
                                className="form-control form-control-sm"
                                min="0"
                                max={selectedQuizForResults.max_points}
                                value={teamPoints[team.team_id] || 0}
                                onChange={(e) => setTeamPoints({
                                  ...teamPoints,
                                  [team.team_id]: parseInt(e.target.value) || 0
                                })}
                              />
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button 
                  type="button" 
                  className="btn btn-secondary" 
                  onClick={handleCloseModal}
                  disabled={submittingResults}
                >
                  Zatvori
                </button>
                <button 
                  type="button" 
                  className="btn btn-primary"
                  onClick={handleSubmitResults}
                  disabled={submittingResults || teams.length === 0}
                >
                  {submittingResults ? 'Spremanje...' : 'Spremi rezultate'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyQuizzesPage;
