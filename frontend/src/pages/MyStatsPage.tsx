import { useMemo } from "react";
import { loadFromStorage, saveToStorage } from "../utils/storage";
import { type Stats } from "../types/Stats";
import { defaultStats } from "../fake/stats";

export default function MyStatsPage() {
  const stats = useMemo(() => {
    const s = loadFromStorage<Stats>("team_stats", defaultStats);
    saveToStorage("team_stats", s); // ensure it exists
    return s;
  }, []);

  return (
    <div className="container my-5" style={{ maxWidth: 1000 }}>
      <h2 className="mb-4">Moja statistika</h2>

      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <div className="text-muted">Odigrani kvizovi</div>
              <div className="display-6 fw-bold">{stats.played}</div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <div className="text-muted">Ukupno bodova</div>
              <div className="display-6 fw-bold">{stats.totalPoints}</div>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <div className="text-muted">Prosj. pozicija</div>
              <div className="display-6 fw-bold">{stats.avgPosition}</div>
            </div>
          </div>
        </div>
      </div>

      <div className="card shadow-sm">
        <div className="card-body">
          <h5 className="card-title mb-3">Povijest sudjelovanja</h5>

          <div className="table-responsive">
            <table className="table table-bordered table-striped align-middle mb-0">
              <thead className="table-dark">
                <tr>
                  <th>Kviz</th>
                  <th>Datum</th>
                  <th>Bodovi</th>
                  <th>Pozicija</th>
                </tr>
              </thead>
              <tbody>
                {stats.history.map((h) => (
                  <tr key={h.id}>
                    <td>{h.quizTitle}</td>
                    <td>{h.date}</td>
                    <td>{h.points}</td>
                    <td>{h.position}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="text-muted small mt-3">
            *Podaci su trenutno “fake” (localStorage).
          </div>
        </div>
      </div>
    </div>
  );
}
