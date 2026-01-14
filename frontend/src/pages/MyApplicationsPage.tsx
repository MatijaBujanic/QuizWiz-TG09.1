import { useMemo, useState } from "react";
import { loadFromStorage, saveToStorage } from "../utils/storage";
import { type Application } from "../types/Application";
import { applications } from "../fake/applications";

export default function MyApplicationsPage() {
  const initial = useMemo(
    () => loadFromStorage<Application[]>("team_applications", applications),
    []
  );

  const [apps, setApps] = useState<Application[]>(initial);

  const persist = (next: Application[]) => {
    setApps(next);
    saveToStorage("team_applications", next);
  };

  const cancelApplication = (id: string) => {
    const next = apps.map((a) =>
      a.id === id ? { ...a, status: "Otkazano" as const } : a
    );
    persist(next);
  };

  return (
    <div className="container my-5" style={{ maxWidth: 1000 }}>
      <h2 className="mb-4">Moje prijave</h2>

      <div className="card shadow-sm">
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-striped align-middle mb-0">
              <thead className="table-dark">
                <tr>
                  <th>Kviz</th>
                  <th>Datum</th>
                  <th>Lokacija</th>
                  <th>Status</th>
                  <th style={{ width: 160 }}>Akcije</th>
                </tr>
              </thead>
              <tbody>
                {apps.map((a) => (
                  <tr key={a.id}>
                    <td>{a.quizTitle}</td>
                    <td>{a.date}</td>
                    <td>{a.location}</td>
                    <td>
                      <span
                        className={`badge ${
                          a.status === "Potvrđen"
                            ? "text-bg-success"
                            : a.status === "Odbijen"
                            ? "text-bg-danger"
                            : a.status === "Otkazano"
                            ? "text-bg-secondary"
                            : "text-bg-warning"
                        }`}
                      >
                        {a.status}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-outline-danger"
                        disabled={
                          a.status === "Otkazano" || a.status === "Odbijen"
                        }
                        onClick={() => cancelApplication(a.id)}
                      >
                        Povuci prijavu
                      </button>
                    </td>
                  </tr>
                ))}
                {apps.length === 0 && (
                  <tr>
                    <td colSpan={5} className="text-center text-muted py-4">
                      Nema prijava.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <div className="text-muted small mt-3">
            *Statusi su trenutno “fake” i spremaju se lokalno.
          </div>
        </div>
      </div>
    </div>
  );
}
