import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { getEmailFromToken } from "../utils/authMapper"; // <-- PROMIJENI PUTANJU da bude ista kao u QuizDetails

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

type Team = {
  points: number;
  rank: number | null;
  members: string[];
  team_id: number;
  team_name: string;
  number_of_members: number;
  quiz_id: number;
};

type TeamsResponse = {
  teams: Team[];
  count: number;
  success: boolean;
};

function formatRank(rank: number | null) {
  if (rank === null) return "—";
  return String(rank);
}

function membersToTextarea(members: string[]) {
  return (members ?? []).join("\n");
}

function textareaToMembers(raw: string) {
  // svaki član u novom redu; izbacimo prazne i trim
  return (raw ?? "")
    .split("\n")
    .map((s) => s.trim())
    .filter(Boolean);
}

export default function MyTeamPage() {
  const { token, isAuthenticated } = useAuth();

  const [me, setMe] = useState<Me | null>(null);

  const [teams, setTeams] = useState<Team[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // edit state (po team_id)
  const [editingTeamId, setEditingTeamId] = useState<number | null>(null);
  const [editTeamName, setEditTeamName] = useState("");
  const [editMembersText, setEditMembersText] = useState("");

  const [busyId, setBusyId] = useState<number | null>(null); // za disable gumba po timu

  const axiosInstance = useMemo(
    () =>
      axios.create({
        baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
        withCredentials: true,
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      }),
    [],
  );

  // 1) load user via /api/users/role
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

  // helper: reload teams
  const reloadTeams = async (userId: number, signal?: AbortSignal) => {
    const res = await axiosInstance.get(`/api/teams/user/${userId}`, {
      signal: (signal as any) ?? undefined,
    });

    const data = res.data as TeamsResponse;
    if (!data.success) throw new Error("Backend je vratio success=false");
    setTeams(data.teams ?? []);
  };

  // 2) load teams for me.user_id
  useEffect(() => {
    if (!isAuthenticated || !me?.user_id) return;

    const controller = new AbortController();

    (async () => {
      setLoading(true);
      setError(null);

      try {
        await reloadTeams(me.user_id, controller.signal);
      } catch (e: any) {
        if (e?.name === "CanceledError" || e?.name === "AbortError") return;
        setError(e?.message || "Dogodila se greška");
      } finally {
        setLoading(false);
      }
    })();

    return () => controller.abort();
  }, [axiosInstance, isAuthenticated, me?.user_id]);

  const startEdit = (t: Team) => {
    setEditingTeamId(t.team_id);
    setEditTeamName(t.team_name);
    setEditMembersText(membersToTextarea(t.members));
  };

  const cancelEdit = () => {
    setEditingTeamId(null);
    setEditTeamName("");
    setEditMembersText("");
  };

  const handleDelete = async (teamId: number) => {
    if (!me?.user_id) return;

    const ok = window.confirm("Sigurno želiš izbrisati ovaj tim?");
    if (!ok) return;

    setBusyId(teamId);
    setError(null);

    try {
        await axiosInstance.delete(`/api/teams/${teamId}`, {
            params: { createdBy: me.user_id }
        });
      // UX: odmah makni iz liste (optimistic), pa eventualno reload
      setTeams((prev) => prev.filter((x) => x.team_id !== teamId));
      if (editingTeamId === teamId) cancelEdit();
    } catch (e: any) {
      console.error(e);
      setError(
        e?.response?.data?.message || e?.message || "Greška pri brisanju tima",
      );
    } finally {
      setBusyId(null);
    }
  };

  const handleUpdate = async (teamId: number) => {
    if (!me?.user_id) return;

    const team_name = editTeamName.trim();
    const members = textareaToMembers(editMembersText);

    if (!team_name) {
      alert("Naziv tima ne smije biti prazan.");
      return;
    }
    if (members.length === 0) {
      alert("Unesi barem jednog člana (svaki u novi red).");
      return;
    }

    setBusyId(teamId);
    setError(null);

    try {
        await axiosInstance.patch(
            `/api/teams/${teamId}`,
            { team_name, members },
            { params: { createdBy: me.user_id } }
        );

      // update lokalno (bez reloada)
      setTeams((prev) =>
        prev.map((t) =>
          t.team_id === teamId
            ? {
              ...t,
              team_name,
              members,
              number_of_members: members.length,
            }
            : t,
        ),
      );

      cancelEdit();
    } catch (e: any) {
      console.error(e);
      setError(
        e?.response?.data?.message ||
        e?.message ||
        "Greška pri ažuriranju tima",
      );
    } finally {
      setBusyId(null);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="container py-4">
        <h2 className="mb-3">Moji timovi</h2>
        <div className="alert alert-warning mb-0">Moraš biti prijavljen.</div>
      </div>
    );
  }

  if (isAuthenticated && !me) {
    return (
      <div className="container py-4">
        <h2 className="mb-3">Moji timovi</h2>
        <div className="alert alert-info mb-0">Učitavam korisnika…</div>
      </div>
    );
  }

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
        <div>
          <h2 className="mb-1">Moji timovi</h2>
          <div className="text-muted">
            Prikaz svih timova u kojima sudjeluješ.
          </div>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-outline-secondary"
            onClick={() => me?.user_id && reloadTeams(me.user_id)}
            disabled={!me?.user_id || loading}
            type="button"
          >
            Osvježi
          </button>

          <Link to="/quizzes" className="btn btn-outline-primary">
            ← Natrag na kvizove
          </Link>
        </div>
      </div>

      {loading && <div className="alert alert-info">Učitavam timove…</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      {!loading && !error && teams.length === 0 && (
        <div className="alert alert-secondary mb-0">Nemaš nijedan tim.</div>
      )}

      <div className="row g-3">
        {teams.map((t) => {
          const isEditing = editingTeamId === t.team_id;
          const isBusy = busyId === t.team_id;

          return (
            <div key={t.team_id} className="col-12 col-md-6 col-lg-4">
              <div className="card h-100 shadow-sm">
                <div className="card-body d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-start gap-2">
                    <div className="flex-grow-1">
                      <div className="text-muted small">Naziv tima</div>

                      {!isEditing ? (
                        <div className="fw-semibold">{t.team_name}</div>
                      ) : (
                        <input
                          className="form-control form-control-sm mt-1"
                          value={editTeamName}
                          onChange={(e) => setEditTeamName(e.target.value)}
                          disabled={isBusy}
                        />
                      )}
                    </div>

                    <span className="badge text-bg-light border">
                      ID: {t.team_id}
                    </span>
                  </div>

                  {t.quiz_id && (
                    <div className="mt-2">
                      <div className="text-muted small">Prijavljen na kviz</div>
                      <Link
                        to={`/quizzes/${t.quiz_id}`}
                        className="text-decoration-none fw-semibold"
                      >
                        Quiz #{t.quiz_id} →
                      </Link>
                    </div>
                  )}

                  <hr />

                  <div className="d-flex gap-3 flex-wrap">
                    <div>
                      <div className="text-muted small">Bodovi</div>
                      <div className="fw-bold">{t.points}</div>
                    </div>

                    <div>
                      <div className="text-muted small">Rang</div>
                      <div className="fw-bold">{formatRank(t.rank)}</div>
                    </div>

                    <div>
                      <div className="text-muted small">Broj članova</div>
                      <div className="fw-bold">
                        {isEditing
                          ? textareaToMembers(editMembersText).length
                          : t.number_of_members}
                      </div>
                    </div>
                  </div>

                  <div className="mt-3">
                    <div className="text-muted small mb-1">Članovi</div>

                    {!isEditing ? (
                      t.members?.length ? (
                        <ul className="mb-0 ps-3">
                          {t.members.map((m) => (
                            <li key={m} className="small">
                              {m}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <div className="small text-muted">—</div>
                      )
                    ) : (
                      <>
                        <textarea
                          className="form-control form-control-sm"
                          rows={5}
                          value={editMembersText}
                          onChange={(e) => setEditMembersText(e.target.value)}
                          placeholder={
                            "Svaki član u novi red\nnpr.\nana@gmail.com\npero@gmail.com"
                          }
                          disabled={isBusy}
                        />
                        <div className="form-text">
                          Svaki član u novi red. Prazne linije se ignoriraju.
                        </div>
                      </>
                    )}
                  </div>

                  {/* buttons */}
                  <div className="mt-auto pt-3 d-flex justify-content-end gap-2 flex-wrap">
                    {!isEditing ? (
                      <>
                        <button
                          className="btn btn-sm btn-outline-primary"
                          type="button"
                          onClick={() => startEdit(t)}
                          disabled={busyId !== null}
                        >
                          Ažuriraj
                        </button>

                        <button
                          className="btn btn-sm btn-outline-danger"
                          type="button"
                          onClick={() => handleDelete(t.team_id)}
                          disabled={busyId !== null}
                        >
                          Izbriši
                        </button>
                      </>
                    ) : (
                      <>
                        <button
                          className="btn btn-sm btn-primary"
                          type="button"
                          onClick={() => handleUpdate(t.team_id)}
                          disabled={isBusy}
                        >
                          Spremi
                        </button>

                        <button
                          className="btn btn-sm btn-outline-secondary"
                          type="button"
                          onClick={cancelEdit}
                          disabled={isBusy}
                        >
                          Odustani
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
