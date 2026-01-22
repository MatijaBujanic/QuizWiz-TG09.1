import { useEffect, useMemo, useState } from "react";
import {
  Link,
  useLocation,
  useNavigate,
  useSearchParams,
} from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { getEmailFromToken } from "../utils/authMapper";

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

type CreateTeamRequest = {
  members: string[];
  team_name: string;
  quiz_id: number;
};

function textareaToMembers(raw: string) {
  return (raw ?? "")
    .split("\n")
    .map((s) => s.trim())
    .filter(Boolean);
}

export default function RegisterPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();

  const { token, isAuthenticated } = useAuth();

  const quizIdFromState = (location.state as any)?.quizId as number | undefined;
  const quizIdFromQuery = Number(searchParams.get("quizId") ?? "");
  const quizId = Number.isFinite(quizIdFromState as any)
    ? (quizIdFromState as number)
    : Number.isFinite(quizIdFromQuery)
      ? quizIdFromQuery
      : NaN;

  const [me, setMe] = useState<Me | null>(null);

  const [teamName, setTeamName] = useState("");
  const [membersText, setMembersText] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [alert, setAlert] = useState<{
    type: "success" | "danger";
    msg: string;
  } | null>(null);

  const axiosInstance = useMemo(
    () =>
      axios.create({
        baseURL: "http://localhost:8080",
        withCredentials: true,
      }),
    [],
  );

  useEffect(() => {
    const loadMe = async () => {
      try {
        if (!isAuthenticated || !token) {
          setMe(null);
          return;
        }

        const email = getEmailFromToken(token);
        if (!email) {
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
        setMe({
          user_id: dto.userId,
          email: dto.email,
          username: dto.username,
          role: dto.role,
        });
      } catch (err) {
        console.error("Failed to load user via /api/users/role:", err);
        setMe(null);
      }
    };

    loadMe();
  }, [axiosInstance, isAuthenticated, token]);

  // default values kad imamo me + quizId
  useEffect(() => {
    if (!me) return;
    if (!quizId || Number.isNaN(quizId)) return;

    // default team name ako je prazno
    setTeamName((prev) => {
      if (prev.trim()) return prev;
      const baseName = me.username || me.email || "Team";
      const safeBase =
        baseName.replace(/[^a-zA-Z0-9-_ ]/g, "").trim() || "Team";
      return `${safeBase}-${quizId}-${Date.now()}`;
    });

    // default members: moj email ako je prazno
    setMembersText((prev) =>
      prev.trim() ? prev : me.email ? `${me.email}\n` : "",
    );
  }, [me, quizId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setAlert(null);

    if (!isAuthenticated) {
      setAlert({ type: "danger", msg: "Moraš biti prijavljen." });
      return;
    }
    if (!me?.user_id) {
      setAlert({
        type: "danger",
        msg: "Ne mogu dohvatiti userId (me.user_id).",
      });
      return;
    }
    if (!quizId || Number.isNaN(quizId)) {
      setAlert({
        type: "danger",
        msg: "Nedostaje quizId. Vrati se na QuizDetails i probaj opet.",
      });
      return;
    }

    const tn = teamName.trim();
    const members = textareaToMembers(membersText);

    if (!tn) {
      setAlert({ type: "danger", msg: "Naziv tima ne smije biti prazan." });
      return;
    }
    if (members.length === 0) {
      setAlert({
        type: "danger",
        msg: "Unesi barem jednog člana (svaki u novi red).",
      });
      return;
    }

    const payload: CreateTeamRequest = {
      team_name: tn,
      quiz_id: quizId,
      members,
    };

    setSubmitting(true);
    try {
      await axiosInstance.post("/api/teams", payload, {
        params: { createdBy: me.user_id },
      });

      setAlert({ type: "success", msg: "Tim uspješno prijavljen!" });

      // kratko pokaži poruku pa redirect (bez “čekaj”, samo odmah redirect)
      navigate("/my-teams", { replace: true });
    } catch (err: any) {
      console.error("Create team failed:", err);

      let msg = "Greška pri prijavi tima.";
      const data = err?.response?.data;

      if (data) {
        if (typeof data === "string") msg = data;
        else if (typeof data === "object" && data.message) msg = data.message;
      } else if (err?.message) {
        msg = err.message;
      }

      setAlert({ type: "danger", msg });
    } finally {
      setSubmitting(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="container py-4">
        <h2 className="mb-3">Prijava tima</h2>
        <div className="alert alert-warning mb-0">Moraš biti prijavljen.</div>
      </div>
    );
  }

  if (!me) {
    return (
      <div className="container py-4">
        <h2 className="mb-3">Prijava tima</h2>
        <div className="alert alert-info mb-0">Učitavam korisnika…</div>
      </div>
    );
  }

  return (
    <div className="container py-4" style={{ maxWidth: 720 }}>
      <div className="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
        <div>
          <h2 className="mb-1">Prijava tima</h2>
          <div className="text-muted">Kreiraj tim i prijavi se na kviz.</div>
        </div>

        <Link to="/quizzes" className="btn btn-outline-secondary">
          ← Natrag
        </Link>
      </div>

      {alert && (
        <div className={`alert alert-${alert.type}`} role="alert">
          {alert.msg}
        </div>
      )}

      <form onSubmit={handleSubmit} className="card shadow-sm">
        <div className="card-body">
          <div className="mb-3">
            <label className="form-label">Quiz ID</label>
            <input
              className="form-control"
              value={Number.isNaN(quizId) ? "" : String(quizId)}
              disabled
            />
            <div className="form-text">
              Ovo polje se automatski popuni iz QuizDetails.
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Naziv tima</label>
            <input
              className="form-control"
              value={teamName}
              onChange={(e) => setTeamName(e.target.value)}
              disabled={submitting}
              placeholder="npr. MojTim-11"
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Članovi</label>
            <textarea
              className="form-control"
              rows={6}
              value={membersText}
              onChange={(e) => setMembersText(e.target.value)}
              disabled={submitting}
              placeholder={
                "Svaki član u novi red\nnpr.\nana@gmail.com\npero@gmail.com"
              }
            />
            <div className="form-text">
              Svaki član u novi red. Prazne linije se ignoriraju.
            </div>
          </div>

          <div className="d-flex justify-content-end gap-2">
            <Link to="/quizzes" className="btn btn-outline-secondary">
              Odustani
            </Link>

            <button
              className="btn btn-primary"
              type="submit"
              disabled={submitting}
            >
              {submitting ? "Šaljem..." : "Prijavi tim"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
