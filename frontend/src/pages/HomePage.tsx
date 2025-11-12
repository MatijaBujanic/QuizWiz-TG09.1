const HomePage = () => {
  return (
    <div className="container">
      <a className="navbar-brand" href="/">
        QuizWizAAAAA
      </a>
      <button
        className="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
      >
        <span className="navbar-toggler-icon"></span>
      </button>

      <div className="collapse navbar-collapse" id="navbarNav">
        <ul className="navbar-nav ms-auto">
          <li className="nav-item">
            <a className="nav-link active" href="/">
              Početna
            </a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="/login">
              Prijava
            </a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="/about">
              O aplikaciji
            </a>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default HomePage;
