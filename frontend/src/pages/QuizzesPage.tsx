import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { type Quiz } from "../types/Quiz";

function startOfDay(d: Date) {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate());
}

function QuizzesPage() {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [search, setSearch] = useState("");
  const [upcomingOnly, setUpcomingOnly] = useState(true);
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [nearestFirst, setNearestFirst] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/api/quizzes")
      .then((res) => {
        console.log("Quizzes:", res.data);
        setQuizzes(res.data);
      })
      .catch(console.error);
  }, []);

  const todayStart = startOfDay(new Date());

  const filteredQuizzes = quizzes
    .filter((q) => {
      const qDate = new Date(q.date);

      if (search.trim()) {
        const s = search.toLowerCase();
        if (
          !q.quiz_name.toLowerCase().includes(s) &&
          !q.quiz_theme.toLowerCase().includes(s)
        ) {
          return false;
        }
      }

      if (upcomingOnly && qDate < todayStart) return false;

      if (fromDate && qDate < startOfDay(new Date(fromDate))) return false;
      if (toDate) {
        const t = new Date(toDate);
        const tEnd = new Date(
          t.getFullYear(),
          t.getMonth(),
          t.getDate(),
          23,
          59,
          59,
        );
        if (qDate > tEnd) return false;
      }

      return true;
    })
    .sort((a, b) => {
      const da = new Date(a.date).getTime();
      const db = new Date(b.date).getTime();
      return nearestFirst ? da - db : db - da;
    });

  return (
    <div className="container mt-4">
      <h2 style={{ color: "#0d6efd" }}>Dostupni kvizovi</h2>

      <div className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-auto" style={{ minWidth: 220 }}>
            <input
              className="form-control"
              placeholder="Pretraži naziv ili temu"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="col-auto">od</div>
          <div className="col-auto">
            <input
              type="date"
              className="form-control"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
            />
          </div>
          <div className="col-auto">do</div>
          <div className="col-auto">
            <input
              type="date"
              className="form-control"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
            />
          </div>

          <div className="col-auto">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                checked={upcomingOnly}
                onChange={(e) => setUpcomingOnly(e.target.checked)}
              />
              <label className="form-check-label">Nadolazeći</label>
            </div>
          </div>

          <div className="col-auto">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                checked={nearestFirst}
                onChange={(e) => setNearestFirst(e.target.checked)}
              />
              <label className="form-check-label">Najbliži prvo</label>
            </div>
          </div>
        </div>
      </div>

      {filteredQuizzes.length === 0 ? (
        <p className="text-muted">Nema rezultata.</p>
      ) : (
        <div className="row g-3">
          <div className="row g-3">
            {filteredQuizzes.map((q) => {
              const statusRaw = (q.status ?? "").toLowerCase();
              const typeRaw = (q.application_type ?? "").toLowerCase();

              const statusHr =
                statusRaw === "open"
                  ? "Otvoren"
                  : statusRaw === "closed"
                    ? "Zatvoren"
                    : statusRaw === "in_progress"
                      ? "U tijeku"
                      : q.status;

              const typeHr =
                typeRaw === "team"
                  ? "Timska"
                  : typeRaw === "individual"
                    ? "Individualna"
                    : q.application_type;

              const statusBorderClass =
                statusRaw === "open"
                  ? "border-success text-success"
                  : statusRaw === "closed"
                    ? "border-danger text-danger"
                    : statusRaw === "in_progress"
                      ? "border-primary text-primary"
                      : "border-secondary text-secondary";

              const ratingDisplay =
                q.average_rating && q.average_rating > 0
                  ? q.average_rating
                  : "-";

              return (
                <div key={q.quiz_id} className="col-12 col-md-6">
                  <div className="card h-100 shadow-sm border-0">
                    <div className="card-body d-flex flex-column pb-6">
                      <h4 className="mb-2" style={{ color: "#0d6efd" }}>
                        {q.quiz_name}
                      </h4>

                      <div className="mb-3">
                        <strong>{q.quiz_theme}</strong>
                      </div>

                      <div className="mb-3">
                        <span className="text-muted">Datum: </span>
                        <strong>{q.date}</strong>
                      </div>

                      <div className="mt-auto">
                        <div className="row g-2 align-items-end">
                          <div className="col-6 col-lg-3">
                            <div className="text-muted small">Ocjena</div>
                            <div className="fw-bold fs-5">{ratingDisplay}</div>
                          </div>

                          <div className="col-6 col-lg-3">
                            <div className="text-muted small">Tip prijave</div>
                            <div className="fw-semibold">{typeHr}</div>
                          </div>

                          <div className="col-6 col-lg-3">
                            <div className="text-muted small">Status</div>
                            <div
                              className={`px-2 py-1 rounded border d-inline-block ${statusBorderClass}`}
                              style={{ backgroundColor: "rgba(0,0,0,0.02)" }}
                            >
                              {statusHr}
                            </div>
                          </div>

                          <div className="col-6 col-lg-3 text-lg-end">
                            <button
                              className="btn btn-primary w-100 w-lg-auto"
                              onClick={() => navigate(`/quizzes/${q.quiz_id}`)}
                            >
                              Detalji
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}

export default QuizzesPage;
