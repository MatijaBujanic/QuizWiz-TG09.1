import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <>
      {/* HERO */}
      <div className="bg-dark text-white py-5">
        <div className="container py-3">
          <div className="row align-items-center g-4">
            <div className="col-lg-7">
              <h1 className="display-5 fw-bold">QuizWiz</h1>
              <p className="lead text-white-50">
                Pronađi kvizove u svom gradu, prijavi tim i prati rezultate —
                sve na jednom mjestu.
              </p>

              <div className="d-flex gap-2 flex-wrap mt-4">
                <Link to="/quizzes" className="btn btn-primary btn-lg">
                  Pregledaj kvizove
                </Link>
                <Link to="/my-teams" className="btn btn-outline-light btn-lg">
                  Pregledaj svoje timove
                </Link>
              </div>
            </div>

            <div className="col-lg-5">
              <div className="card bg-light text-dark shadow-sm">
                <div className="card-body">
                  <h5 className="card-title mb-3">Što možeš raditi?</h5>
                  <ul className="list-group list-group-flush">
                    <li className="list-group-item">
                      🔎 Pretraži kvizove po nazivu, lokaciji i datumu
                    </li>
                    <li className="list-group-item">
                      👥 Prijavi tim dok ima slobodnih mjesta
                    </li>
                    <li className="list-group-item">
                      🏆 Pregledaj rezultate i poredak
                    </li>
                    <li className="list-group-item">
                      ⭐ Ocijeni kviz i ostavi recenziju
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* “How it works” */}
      <div className="container my-5">
        <h2 className="mb-4">Kako funkcionira</h2>
        <div className="row g-3">
          <div className="col-md-4">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">1) Nađi kviz</h5>
                <p className="card-text text-muted">
                  Filtriraj po lokaciji i datumu, i pronađi kviz koji ti
                  odgovara.
                </p>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">2) Prijavi tim</h5>
                <p className="card-text text-muted">
                  Prijava traje par sekundi – dok kviz ne popuni maksimalan broj
                  timova.
                </p>
              </div>
            </div>
          </div>
          <div className="col-md-4">
            <div className="card h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">3) Prati rezultate</h5>
                <p className="card-text text-muted">
                  Nakon kviza vidi poredak i statistiku sudjelovanja.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* CTA */}
        <div className="card mt-5 border-0 bg-light">
          <div className="card-body d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3">
            <div>
              <h4 className="mb-1">Spreman za prvi kviz?</h4>
              <div className="text-muted">
                Kreni s pregledom dostupnih kvizova.
              </div>
            </div>
            <div className="d-flex gap-2">
              <Link to="/quizzes" className="btn btn-primary">
                Otvori kvizove
              </Link>
              <Link to="/about" className="btn btn-outline-secondary">
                O aplikaciji
              </Link>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
