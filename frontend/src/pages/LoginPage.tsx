export default function LoginPage() {
  const BACKEND_URL = "http://localhost:8080";

  const handleLogin = (provider: "google" | "github") => {
    window.location.href = `${BACKEND_URL}/oauth2/authorization/${provider}`;
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <div className="bg-white shadow-md rounded-2xl p-8 w-80 text-center">
        <h1 className="text-2xl font-semibold mb-6">Prijava</h1>

        <button
          onClick={() => handleLogin("google")}
          className="btn btn-outline-light"
        >
          <h2>🔴 Prijava putem Google-a</h2>
        </button>

        <button
          onClick={() => handleLogin("github")}
          className="btn btn-outline-light"
        >
          <h2>🐱 Prijava putem GitHub-a</h2>
        </button>

        <p className="text-sm text-gray-500 mt-6">
          Nakon prijave bit ćete preusmjereni natrag u aplikaciju.
        </p>
      </div>
    </div>
  );
}