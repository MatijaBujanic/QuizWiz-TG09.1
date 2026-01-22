export default function AboutPage() {
  return (
    <div className="container my-5">
      <div className="mb-5 text-center">
        <h1 className="mb-3">O aplikaciji QuizWiz</h1>
        <p className="text-muted fs-5">
          QuizWiz je web aplikacija za organizaciju i praćenje pub kvizova,
          namijenjena organizatorima i sudionicima koji žele jednostavno
          upravljati prijavama, rezultatima i sudjelovanjem.
        </p>
      </div>

      <div className="row mb-5">
        <div className="col-md-6 mb-3">
          <div className="card h-100 shadow-sm">
            <div className="card-body">
              <h4 className="card-title">🎯 Cilj aplikacije</h4>
              <p className="card-text text-muted">
                Cilj QuizWiz aplikacije je olakšati proces organizacije pub
                kvizova te omogućiti sudionicima jednostavan pregled dostupnih
                kvizova, prijavu timova i praćenje rezultata.
              </p>
            </div>
          </div>
        </div>

        <div className="col-md-6 mb-3">
          <div className="card h-100 shadow-sm">
            <div className="card-body">
              <h4 className="card-title">⚙️ Funkcionalnosti</h4>
              <ul className="text-muted mb-0">
                <li>Pregled i pretraživanje kvizova</li>
                <li>Prijava timova na kvizove</li>
                <li>Upravljanje kvizovima za organizatore</li>
                <li>Pregled rezultata i poretka</li>
                <li>Ocjenjivanje i recenzije kvizova</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div className="row mb-5">
        <div className="col-12">
          <div className="card h-100 shadow-sm bg-primary text-white">
            <div className="card-body text-center">
              <h4 className="card-title mb-3">Želiš postati organizator?</h4>

              <p className="card-text fs-5">
                Organizatori se ne prijavljuju putem aplikacije.
                <br />
                Ako želiš organizirati pub kvizove putem QuizWiz platforme, javi
                nam se i dogovorit ćemo suradnju.
              </p>

              <p className="mb-0">
                Kontakt email:{" "}
                <a
                  href="mailto:quizwiz.support@gmail.com"
                  className="text-white fw-semibold text-decoration-underline"
                >
                  wizionari.progi@gmail.com
                </a>
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="mb-5">
        <h2 className="mb-4 text-center">Tim</h2>

        <div className="row justify-content-center g-3">
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Mihael Vrančić</h5>
              </div>
            </div>
          </div>
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Paula Vidak</h5>
              </div>
            </div>
          </div>
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Jona Matsumoto Šegota</h5>
              </div>
            </div>
          </div>
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Dario Herceg</h5>
              </div>
            </div>
          </div>
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Filip Gojak</h5>
              </div>
            </div>
          </div>
          <div className="col-md-4 col-sm-6">
            <div className="card text-center h-100 shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Ana Kasanić</h5>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="mb-5">
        <h2 className="mb-4 text-center">Voditelj projekta</h2>

        <div className="row justify-content-center">
          <div className="col-md-6">
            <div className="card text-center shadow-sm">
              <div className="card-body">
                <h5 className="card-title">Matija Bujanić</h5>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
