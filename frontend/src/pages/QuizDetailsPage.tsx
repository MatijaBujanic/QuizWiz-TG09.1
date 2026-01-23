import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import GoogleMap from "../components/GoogleMap";
import type { Quiz } from "../types/Quiz";
import type { Location } from "../types/Location";
import axios from "axios";
import StarRatingControl from "../components/StarRatingControl";

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

type TeamRanking = {
  team_id: number;
  team_name: string;
  points: number;
  rank: number | null;
  number_of_members: number;
  members: string[];
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
  const [geocodedLocation, setGeocodedLocation] = useState<{
    lat: number;
    lng: number;
  } | null>(null);

  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY as string;
  const mapCenter = useMemo(() => ({ lat: 45.815, lng: 15.9819 }), []);

  const [me, setMe] = useState<Me | null>(null);
  const [applying] = useState(false);
  const [applyError] = useState<string | null>(null);
  const [ranking, setRanking] = useState<TeamRanking[]>([]);
  const [rankingLoading, setRankingLoading] = useState(false);

  const navigate = useNavigate();
  const { token, isAuthenticated } = useAuth();

  const axiosInstance = useMemo(
    () =>
      axios.create({
        baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
        withCredentials: true,
      }),
    [],
  );

  useEffect(() => {
    const geocodeAddress = async () => {
      if (!location?.address || !apiKey) {
        setGeocodedLocation(null);
        return;
      }

      try {
        let geocodeQuery = location.address;
        if (location.city) {
          geocodeQuery += `, ${location.city}, Croatia`;
        } else {
          geocodeQuery += `, Croatia`;
        }

        const response = await fetch(
          `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(geocodeQuery)}&key=${apiKey}&language=hr&region=hr`,
        );
        const data = await response.json();
        if (data.results && data.results.length > 0) {
          const { lat, lng } = data.results[0].geometry.location;
          setGeocodedLocation({ lat, lng });
          console.log("Geocoded location:", {
            lat,
            lng,
            address: geocodeQuery,
          });
        } else {
          console.warn("No geocoding results for:", geocodeQuery);
          setGeocodedLocation(null);
        }
      } catch (error) {
        console.error("Geocoding error:", error);
        setGeocodedLocation(null);
      }
    };

    geocodeAddress();
  }, [location, apiKey]);

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
          { params: { email } },
        );

        const dto = res.data;

        const mapped: Me = {
          user_id: dto.userId,
          email: dto.email,
          username: dto.username,
          role: dto.role,
        };

        console.log("User data from /api/users/role:", mapped);
        setMe(mapped);
      } catch (err) {
        console.error("Failed to load user via /api/users/role:", err);
        setMe(null);
      }
    };

    loadMe();
  }, [axiosInstance, isAuthenticated, token]);

  const handleApply = () => {
    if (!quiz) return;
    navigate("/register", { state: { quizId: quiz.quiz_id } });
  };

  const loadRanking = async () => {
    if (!id) return;

    setRankingLoading(true);
    try {
      const res = await axiosInstance.get(`/api/teams/quiz/${id}/ranking`);
      if (res.data.success) {
        setRanking(res.data.ranking || []);
      }
    } catch (e) {
      console.error("Failed to load ranking:", e);
      setRanking([]);
    } finally {
      setRankingLoading(false);
    }
  };

  const reloadQuiz = async () => {
    if (!id) return;

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

  useEffect(() => {
    setError("");
    setQuiz(null);
    setLocation(null);
    reloadQuiz();
    loadRanking();
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
        </div>

        <div className="text-end">
          {me?.user_id && (
            <StarRatingControl quizId={quiz.quiz_id} onChanged={reloadQuiz} />
          )}
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
                <GoogleMap
                  apiKey={apiKey}
                  center={geocodedLocation || mapCenter}
                  zoom={geocodedLocation ? 15 : 13}
                  marker={
                    geocodedLocation
                      ? {
                        lat: geocodedLocation.lat,
                        lng: geocodedLocation.lng,
                        title: location?.location_name || "Lokacija kviza",
                      }
                      : undefined
                  }
                />
              </div>
              {location && (
                <div className="text-muted small mt-2">
                  📍 {location.location_name}
                  {location.address && ` - ${location.address}`}
                  {location.city && `, ${location.city}`}
                </div>
              )}
            </div>
          </div>

          <div className="card shadow-sm border-0">
            <div className="card-body">
              {applyError && (
                <div className="alert alert-danger small mb-3">
                  {applyError}
                </div>
              )}

              <div className="d-grid gap-2">
                <button
                  className="btn btn-primary btn-lg"
                  onClick={handleApply}
                  disabled={applying || !me}
                >
                  {applying ? "Prijavljivanje..." : "Prijavi se"}
                </button>
              </div>
              {!me && (
                <p className="text-muted small mt-2 mb-0">
                  Trebate biti prijavljeni da se prijavite na kviz
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Rang lista */}
      <div className="row g-3 mt-3">
        <div className="col-12">
          <div className="card shadow-sm border-0">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="mb-0">Rang lista timova</h5>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={loadRanking}
                  disabled={rankingLoading}
                >
                  {rankingLoading ? "Učitavanje..." : "Osvježi"}
                </button>
              </div>

              {rankingLoading && (
                <div className="text-center py-4">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Učitavanje...</span>
                  </div>
                </div>
              )}

              {!rankingLoading && ranking.length === 0 && (
                <div className="alert alert-info mb-0">
                  Trenutno nema prijavljenih timova ili rezultati još nisu uneseni.
                </div>
              )}

              {!rankingLoading && ranking.length > 0 && (
                <div className="table-responsive">
                  <table className="table table-hover mb-0">
                    <thead>
                      <tr>
                        <th style={{ width: "80px" }}>Rang</th>
                        <th>Naziv tima</th>
                        <th style={{ width: "120px" }}>Bodovi</th>
                        <th style={{ width: "120px" }}>Članovi</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ranking.map((team) => (
                        <tr key={team.team_id}>
                          <td>
                            <span
                              className={
                                team.rank === 1
                                  ? "badge bg-warning text-dark fs-6"
                                  : team.rank === 2
                                    ? "badge bg-secondary fs-6"
                                    : team.rank === 3
                                      ? "badge bg-info text-dark fs-6"
                                      : "badge bg-light text-dark fs-6"
                              }
                            >
                              {team.rank !== null ? `#${team.rank}` : "-"}
                            </span>
                          </td>
                          <td className="fw-semibold">{team.team_name}</td>
                          <td>
                            <span className="badge bg-primary">
                              {team.points} bodova
                            </span>
                          </td>
                          <td>{team.number_of_members}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
