import { useState } from "react";
import QuizCard from "../components/QuizCard";
import { quizzes } from "../fake/quizzes"; // const response = await fetch("/api/quizzes");

function startOfDay(d: Date) {
  return new Date(d.getFullYear(), d.getMonth(), d.getDate());
}

function QuizzesPage() {
  const [search, setSearch] = useState("");
  const [location, setLocation] = useState("All");
  const [upcomingOnly, setUpcomingOnly] = useState(true);
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [nearestFirst, setNearestFirst] = useState(true);

  const locations = [
    "All",
    ...Array.from(new Set(quizzes.map((q) => q.location))),
  ];

  function parseQuizDate(dateStr: string) {
    return new Date(dateStr.replace(" ", "T"));
  }

  const todayStart = startOfDay(new Date());

  const filteredQuizzes = quizzes
    .filter((q) => {
      const qDate = parseQuizDate(q.date);

      if (search.trim()) {
        const s = search.trim().toLowerCase();
        const inTitle = q.title.toLowerCase().includes(s);
        const inDesc = q.description.toLowerCase().includes(s);
        if (!inTitle && !inDesc) return false;
      }

      if (location !== "All" && q.location !== location) return false;

      if (upcomingOnly && qDate < todayStart) return false;

      if (fromDate) {
        const f = new Date(fromDate);
        if (qDate < startOfDay(f)) return false;
      }
      if (toDate) {
        const t = new Date(toDate);
        const tEnd = new Date(
          t.getFullYear(),
          t.getMonth(),
          t.getDate(),
          23,
          59,
          59
        );
        if (qDate > tEnd) return false;
      }

      return true;
    })
    .sort((a, b) => {
      const da = parseQuizDate(a.date).getTime();
      const db = parseQuizDate(b.date).getTime();
      return nearestFirst ? da - db : db - da;
    });

  return (
    <div className="container mt-4">
      <h2 style={{ color: "#0d6efd" }}>Dostupni kvizovi</h2>

      <div className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-auto" style={{ minWidth: 220 }}>
            <input
              type="text"
              className="form-control"
              placeholder="Pretraži naslov ili opis"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div className="col-auto">
            <select
              className="form-select"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
            >
              {locations.map((loc) => (
                <option key={loc} value={loc}>
                  {loc}
                </option>
              ))}
            </select>
          </div>

          <div className="col-auto">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                id="upcomingOnly"
                checked={upcomingOnly}
                onChange={(e) => setUpcomingOnly(e.target.checked)}
              />
              <label className="form-check-label" htmlFor="upcomingOnly">
                Nadolazeći
              </label>
            </div>
          </div>

          <div className="col-auto">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                id="nearestFirst"
                checked={nearestFirst}
                onChange={(e) => setNearestFirst(e.target.checked)}
              />
              <label className="form-check-label" htmlFor="nearestFirst">
                Najbliži prvo
              </label>
            </div>
          </div>

          <div className="col-auto">
            <input
              className="form-control"
              type="date"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
            />
          </div>
          <div className="col-auto">
            <input
              className="form-control"
              type="date"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
            />
          </div>
        </div>
      </div>

      {filteredQuizzes.length === 0 ? (
        <p className="text-muted">Nema rezultata za zadane kriterije.</p>
      ) : (
        filteredQuizzes.map((quiz) => <QuizCard key={quiz.id} quiz={quiz} />)
      )}
    </div>
  );
}

export default QuizzesPage;
