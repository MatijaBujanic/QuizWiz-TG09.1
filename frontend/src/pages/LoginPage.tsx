function LoginPage() {
  const BACKEND_URL = "http://localhost:8080";

  const handleLogin = (provider: "google" | "github") => {
    window.location.href = `${BACKEND_URL}/oauth2/authorization/${provider}`;
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
      <div className="card shadow p-4" style={{ width: "350px" }}>
        <h3 className="text-center mb-4 fw-bold">Prijava</h3>

        <button
          onClick={() => handleLogin("google")}
          className="btn btn-outline-primary w-100 mb-3 d-flex align-items-center justify-content-center"
        >
          <i className="bi bi-google me-2"></i>
          Prijava putem Google-a
        </button>

        <button
          onClick={() => handleLogin("github")}
          className="btn btn-outline-secondary w-100 d-flex align-items-center justify-content-center"
        >
          <i className="bi bi-github me-2"></i>
          Prijava putem GitHub-a
        </button>

        <p
          className="text-center text-muted mt-4 mb-0"
          style={{ fontSize: "0.9rem" }}
        >
          Nakon prijave bit ćete preusmjereni natrag u aplikaciju.
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
