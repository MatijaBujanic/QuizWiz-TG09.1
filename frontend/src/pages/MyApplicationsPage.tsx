import { useEffect, useState } from "react";
import axios from "axios";

// DTOs aligned s /users/me/quiz-history
interface UserTeam {
  team_id?: number;
  team_name: string;
  application_status?: string;
  number_of_members?: number;
  members?: string[];
}

interface QuizHistory {
  quiz_id: number;
  quiz_name: string;
  quiz_theme?: string;
  date?: string;
  time?: string;
  status?: string;
  location_id?: number;
  location_name?: string;
  user_team?: UserTeam | null;
}

interface ApplicationEntry {
  team: UserTeam;
  quiz: QuizHistory;
  source: "remote" | "local";
}

interface MeLite {
  email?: string;
  username?: string;
}

interface LocalApplication {
  quiz_id: number;
  quiz_name: string;
  date?: string;
  location_name?: string;
  team_name: string;
  status?: string;
  email?: string;
}

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});

export default function MyApplicationsPage() {
  const [applications, setApplications] = useState<ApplicationEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [me, setMe] = useState<MeLite | null>(null);

  // Dohvati user info za filtriranje lokalnih prijava
  useEffect(() => {
    const loadMe = async () => {
      try {
        const res = await axiosInstance.get("/users/me");
        if (typeof res.data === "string" && res.data.includes("<!DOCTYPE html>")) {
          setError("Niste prijavljeni. Molimo prijavite se ponovno.");
          setLoading(false);
          setTimeout(() => { window.location.href = "/login"; }, 2000);
          return;
        }
        setMe({ email: res.data.email, username: res.data.username });
      } catch (e) {
        console.error("Could not load user info", e);
        setMe(null);
      }
    };
    loadMe();
  }, []);

  // Dohvati povijest kvizova i izvuci user_team kao prijave + lokalni fallback
  useEffect(() => {
    const fetchApplications = async () => {
      setLoading(true);
      setError(null);

      try {
        const res = await axiosInstance.get("/users/me/quiz-history");
        console.log("Quiz history:", res.data);

        // Login redirect (HTML) guard
        if (typeof res.data === "string" && res.data.includes("<!DOCTYPE html>")) {
          setError("Niste prijavljeni. Molimo prijavite se ponovno.");
          setLoading(false);
          setTimeout(() => { window.location.href = "/login"; }, 2000);
          return;
        }

        const history: QuizHistory[] = Array.isArray(res.data)
          ? res.data
          : res.data?.history || [];

        const remoteApps: ApplicationEntry[] = history
          .filter((item) => item.user_team)
          .map((item) => ({
            quiz: item,
            team: item.user_team as UserTeam,
            source: "remote",
          }));

        // Lokalni fallback (zapis nakon prijave iz QuizDetailsPage)
        const localRaw = localStorage.getItem("local_applications");
        const localApps: LocalApplication[] = localRaw ? JSON.parse(localRaw) : [];

        const filteredLocal = me?.email
          ? localApps.filter((la) => la.email === me.email)
          : localApps;

        const localEntries: ApplicationEntry[] = filteredLocal.map((la) => ({
          source: "local",
          quiz: {
            quiz_id: la.quiz_id,
            quiz_name: la.quiz_name,
            date: la.date,
            location_name: la.location_name,
            status: la.status,
          },
          team: {
            team_name: la.team_name,
            application_status: la.status,
          },
        }));

        setApplications([...remoteApps, ...localEntries]);
      } catch (err: any) {
        console.error("Error fetching applications:", err);
        setError(err.response?.data?.message || "Greška pri učitavanju prijava");
      } finally {
        setLoading(false);
      }
    };

    fetchApplications();
  }, [me?.email]);

  const cancelApplication = async (
    teamId: number | undefined,
    source: "remote" | "local",
    teamName?: string
  ) => {
    if (source === "local" && teamId === undefined) {
      // Lokalni zapis iz cache-a - samo ukloni lokalno
      setApplications((prev) => prev.filter((app) => app.team.team_name !== teamName || app.source !== "local"));
      try {
        const localRaw = localStorage.getItem("local_applications");
        const localApps: LocalApplication[] = localRaw ? JSON.parse(localRaw) : [];
        const next = localApps.filter((la) => la.team_name !== teamName);
        localStorage.setItem("local_applications", JSON.stringify(next));
      } catch {}
      return;
    }

    if (!teamId) {
      alert("Nije moguće povući prijavu jer ID tima nedostaje.");
      return;
    }

    if (!window.confirm("Jeste li sigurni da želite povući prijavu?")) return;

    try {
      await axiosInstance.delete(`/api/teams/${teamId}`);
      setApplications((prev) => prev.filter((app) => app.team.team_id !== teamId));
      alert("Prijava je povučena");
    } catch (err: any) {
      console.error("Error canceling application:", err);
      alert(err.response?.data?.message || "Greška pri povlačenju prijave");
    }
  };

  if (loading) {
    return (
      <div className="container my-5" style={{ maxWidth: 1000 }}>
        <h2 className="mb-4">Moje prijave</h2>
        <p>Učitavanje...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container my-5" style={{ maxWidth: 1000 }}>
        <h2 className="mb-4">Moje prijave</h2>
        <div className="alert alert-danger">{error}</div>
      </div>
    );
  }

  return (
    <div className="container my-5" style={{ maxWidth: 1000 }}>
      <h2 className="mb-4">Moje prijave</h2>

      <div className="card shadow-sm">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped align-middle mb-0">
              <thead className="table-dark">
                <tr>
                  <th>Tim</th>
                  <th>Kviz</th>
                  <th>Datum</th>
                  <th>Lokacija</th>
                  <th>Status</th>
                  <th style={{ width: 160 }}>Akcije</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((app, idx) => (
                  <tr key={`${app.quiz.quiz_id}-${app.team.team_id ?? idx}`}>
                    <td>{app.team.team_name}</td>
                    <td>{app.quiz.quiz_name || "-"}</td>
                    <td>{app.quiz.date || "-"}</td>
                    <td>{app.quiz.location_name || "-"}</td>
                    <td>
                      <span className="badge text-bg-success">
                        {app.team.application_status || app.quiz.status || "Aktivan"}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-outline-danger"
                        onClick={() => cancelApplication(app.team.team_id, app.source, app.team.team_name)}
                      >
                        Povuci prijavu
                      </button>
                    </td>
                  </tr>
                ))}
                {applications.length === 0 && (
                  <tr>
                    <td colSpan={6} className="text-center text-muted py-4">
                      Nema prijava. Prijavite se na kviz da biste vidjeli svoje timove ovdje.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
