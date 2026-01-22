import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

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
  location_id?: number;
}

// Kreiraj axios instancu s withCredentials
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // Šalji session cookies
});

const MyQuizzesPage: React.FC = () => {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async (quizId: number) => {
    if (!window.confirm('Jeste li sigurni da želite obrisati ovaj kviz?')) {
      return;
    }

    try {
      await axiosInstance.delete(`/api/organizer/quizzes/${quizId}`);
      
      // Ukloni kviz iz state-a
      setQuizzes(prev => prev.filter(q => q.quiz_id !== quizId));
      
      // Ukloni iz lokalnog cache-a ako postoji
      try {
        const localRaw = localStorage.getItem('local_created_quizzes');
        if (localRaw) {
          const localQuizzes = JSON.parse(localRaw);
          const updated = localQuizzes.filter((q: Quiz) => q.quiz_id !== quizId);
          localStorage.setItem('local_created_quizzes', JSON.stringify(updated));
        }
      } catch (cacheErr) {
        console.warn('Could not update local cache', cacheErr);
      }
      
      alert('Kviz je uspješno obrisan!');
    } catch (err: any) {
      console.error('Greška pri brisanju kviza:', err);
      alert(err.response?.data?.message || 'Greška pri brisanju kviza');
    }
  };

  const handleEdit = (quizId: number) => {
    // Navigiraj na edit stranicu (može biti novi route ili postojeći CreateQuizPage s edit modom)
    navigate(`/edit-quiz/${quizId}`);
  };

  useEffect(() => {
    const fetchQuizzes = async () => {
      try {
        // Prvo provjeri postoji li user u bazi
        const meResponse = await axiosInstance.get('/users/me');
        console.log('User /users/me:', meResponse.data);
        
        if (!meResponse.data.user_id) {
          console.warn('User nema user_id - pokušavam alternativni endpoint /users/me/organized-quizzes');
          
          // Fallback: pokušaj /users/me/organized-quizzes
          try {
            const altResponse = await axiosInstance.get('/users/me/organized-quizzes');
            console.log('Alternative endpoint response:', altResponse.data);
            const quizzesData = Array.isArray(altResponse.data) ? altResponse.data : [];
            
            // Mapuj QuizHistoryResponse na Quiz interface
            const mappedQuizzes = quizzesData.map((qh: any) => ({
              quiz_id: qh.quiz_id,
              quiz_name: qh.quiz_name || qh.quizName,
              quiz_theme: qh.quiz_theme || qh.quizTheme || '',
              description: qh.description || '',
              date: qh.date || '',
              time: qh.time || '',
              application_type: 'team',
              number_of_rounds: qh.number_of_rounds || qh.numberOfRounds || 0,
              max_points: qh.max_points || qh.maxPoints || 0,
              status: qh.status || 'open',
              location_id: qh.location_id || qh.locationId,
            }));
            
            if (mappedQuizzes.length > 0) {
              setQuizzes(mappedQuizzes);
              setLoading(false);
              return;
            }
            
            // Ako je prazan, pokušaj lokalni fallback
            console.warn('Alternative endpoint vratio prazan array - koristim lokalni cache');
          } catch (altErr) {
            console.error('Alternative endpoint failed:', altErr);
          }
          
          // Lokalni fallback - kvizovi spremljeni pri kreiranju
          try {
            const localRaw = localStorage.getItem('local_created_quizzes');
            const localQuizzes = localRaw ? JSON.parse(localRaw) : [];
            if (localQuizzes.length > 0) {
              console.log('Prikazujem kvizove iz lokalnog cache-a:', localQuizzes.length);
              setQuizzes(localQuizzes);
              setLoading(false);
              return;
            }
          } catch (cacheErr) {
            console.error('Could not load local cache', cacheErr);
          }
          
          setError('Vaš korisnički račun nije pronađen u bazi podataka. Potrebno je da administrator doda vaš email u tablicu users s ulogom ORGANIZER.');
          setLoading(false);
          return;
        }
        
        const response = await axiosInstance.get('/api/organizer/quizzes');
        const quizzesData = Array.isArray(response.data) ? response.data : (response.data.quizzes || []);
        setQuizzes(quizzesData);
      } catch (err: any) {
        console.error('Greška pri učitavanju kvizova:', err);

        // HTML (login redirect)
        if (typeof err.response?.data === 'string' && err.response.data.includes('<!DOCTYPE')) {
          setError('Niste prijavljeni ili sesija je istekla. Prijavite se ponovno.');
          return;
        }

        // Friendly hint for 500 (često kad user nije u bazi/organizer_id je null)
        if (err.response?.status === 500) {
          setError('Greška (500) pri dohvaćanju kvizova. Provjerite da ste prijavljeni i da vaš račun postoji u bazi s ulogom ORGANIZER.');
          return;
        }

        setError(err.response?.data?.message || 'Greška pri učitavanju kvizova');
      } finally {
        setLoading(false);
      }
    };
    fetchQuizzes();
  }, []);

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
                        quiz.status === 'closed' ? 'bg-danger' :
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
    </div>
  );
};

export default MyQuizzesPage;
