import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";
import GoogleMap from "../components/GoogleMap";
import type { Quiz } from "../types/Quiz";
import type { Location } from "../types/Location";
import axios from "axios";
import StarRatingControl from "../components/StarRatingControl";

type Me = {
  user_id: number;
  email: string;
  username: string;
  role: string;
};

function statusHr(raw: string) {
  const s = (raw ?? "").toLowerCase();
  if (s === "open") return "Otvoren";
  if (s === "closed") return "Zatvoren";
  if (s === "in_progress") return "U tijeku";
  return raw;
}

function appTypeHr(raw: string) {
  const t = (raw ?? "").toLowerCase();
  if (t === "team") return "Timska";
  if (t === "individual") return "Individualna";
  return raw;
}

function formatTime(t: Quiz["time"]) {
  if (!t) return null;
  if (typeof t === "string") return t.slice(0, 5);
  const hh = String(t.hour).padStart(2, "0");
  const mm = String(t.minute).padStart(2, "0");
  return `${hh}:${mm}`;
}

export default function QuizDetailsPage() {
  const { id } = useParams();
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [location, setLocation] = useState<Location | null>(null);
  const [error, setError] = useState("");

  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY as string;

  const mapCenter = useMemo(() => ({ lat: 45.815, lng: 15.9819 }), []);

  const [me, setMe] = useState<Me | null>(null);

  useEffect(() => {
    const loadMe = async () => {
      try {
        const res = await axios.get<Me>("/users/me", { withCredentials: true });
        setMe(res.data);
      } catch {
        setMe(null);
      }
    };
    loadMe();
  }, []);

  useEffect(() => {
    if (!id) return;

    const load = async () => {
      setError("");
      setQuiz(null);
      setLocation(null);

      try {
        const quizRes = await api.get(`/api/quizzes/${id}`);
        const q: Quiz = quizRes.data;
        setQuiz(q);

        if (q.location_id != null) {
          try {
            const locRes = await api.get(`/api/locations/${q.location_id}`);
            setLocation(locRes.data);
          } catch (e) {
            console.error("Location fetch failed:", e);
          }
        }
      } catch (e) {
        console.error(e);
        setError("Ne mogu dohvatiti detalje kviza.");
      }
    };

    load();
  }, [id]);

  const ratingDisplay = useMemo(() => {
    if (!quiz) return "-";
    return quiz.average_rating != null && quiz.average_rating > 0
      ? quiz.average_rating
      : "-";
  }, [quiz]);

  if (error) {
    return (
      <div className="container mt-4">
        <div className="alert alert-danger">{error}</div>
        <Link to="/quizzes" className="btn btn-outline-secondary">
          ← Natrag na kvizove
        </Link>
      </div>
    );
  }

  if (!quiz) {
    return (
      <div className="container mt-4">
        <div className="card shadow-sm border-0">
          <div className="card-body">Učitavanje...</div>
        </div>
      </div>
    );
  }

  const timeText = formatTime(quiz.time);
  const createdAtText = quiz.created_at
    ? new Date(quiz.created_at).toLocaleString()
    : "-";

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
        <div>
          <Link to="/quizzes" className="text-decoration-none">
            ← Natrag na kvizove
          </Link>

          <h2 className="mt-2 mb-1" style={{ color: "#0d6efd" }}>
            {quiz.quiz_name}
          </h2>

          <div className="text-muted mb-2">{quiz.quiz_theme}</div>
          <div className="small text-muted">
            DEBUG me: {me ? JSON.stringify(me) : "null"}
          </div>
          {me?.user_id && (
            <StarRatingControl quizId={quiz.quiz_id} onChanged={() => {}} />
          )}
        </div>

        <div className="text-end">
          <div className="text-muted small">Ocjena</div>
          <div className="fw-bold fs-2" style={{ lineHeight: 1 }}>
            {ratingDisplay}
          </div>
          <div className="text-muted small">{quiz.rating_count} ocjena</div>
        </div>
      </div>

      <div className="row g-3">
        <div className="col-12 col-lg-7">
          <div className="card shadow-sm border-0 h-100">
            <div className="card-body">
              <h5 className="mb-3">Detalji kviza</h5>

              <div className="row gy-2">
                <div className="col-5 col-md-4 text-muted">Datum</div>
                <div className="col-7 col-md-8 fw-semibold">{quiz.date}</div>

                <div className="col-5 col-md-4 text-muted">Vrijeme</div>
                <div className="col-7 col-md-8">
                  {timeText ?? <span className="text-muted">-</span>}
                </div>

                <div className="col-5 col-md-4 text-muted">Status</div>
                <div className="col-7 col-md-8">
                  <span className="badge bg-secondary">
                    {statusHr(quiz.status)}
                  </span>
                </div>

                <div className="col-5 col-md-4 text-muted">Tip prijave</div>
                <div className="col-7 col-md-8">
                  {appTypeHr(quiz.application_type)}
                </div>

                <div className="col-5 col-md-4 text-muted">Broj rundi</div>
                <div className="col-7 col-md-8">{quiz.number_of_rounds}</div>

                <div className="col-5 col-md-4 text-muted">Max bodova</div>
                <div className="col-7 col-md-8">{quiz.max_points}</div>

                <div className="col-5 col-md-4 text-muted">Kreirano</div>
                <div className="col-7 col-md-8">{createdAtText}</div>
              </div>

              <hr className="my-4" />

              <h5 className="mb-2">Opis</h5>
              <p className="mb-0">
                {quiz.description ? (
                  quiz.description
                ) : (
                  <span className="text-muted">Nema opisa.</span>
                )}
              </p>
            </div>
          </div>
        </div>

        <div className="col-12 col-lg-5">
          <div className="card shadow-sm border-0 mb-3">
            <div className="card-body">
              <h5 className="mb-3">Lokacija</h5>

              {quiz.location_id == null ? (
                <div className="text-muted">
                  Lokacija nije postavljena za ovaj kviz.
                </div>
              ) : (
                <>
                  <div className="mb-2">
                    <div className="text-muted small">Naziv</div>
                    <div className="fw-semibold">
                      {location?.location_name ?? (
                        <span className="text-muted">Učitavanje...</span>
                      )}
                    </div>
                  </div>

                  <div>
                    <div className="text-muted small">Adresa</div>
                    <div>
                      {location?.address ?? (
                        <span className="text-muted">-</span>
                      )}
                      {location?.city ? `, ${location.city}` : ""}
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>
          <div className="card shadow-sm border-0 mb-3">
            <div className="card-body">
              <h5 className="mb-3">Karta</h5>

              <div style={{ height: 280 }}>
                <GoogleMap apiKey={apiKey} center={mapCenter} zoom={13} />
              </div>

              <div className="text-muted small mt-2">
                (Karta je preview — adresa se ne pozicionira automatski jer
                nemamo koordinate.)
              </div>
            </div>
          </div>

          <div className="card shadow-sm border-0">
            <div className="card-body">
              <h5 className="mb-3">Akcije</h5>

              <div className="d-grid gap-2">
                <button className="btn btn-primary btn-lg" disabled>
                  Prijavi se
                </button>
                <button className="btn btn-outline-primary btn-lg" disabled>
                  Timovi
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
