import { useSearchParams } from 'react-router-dom';

export default function Login() {
  const [searchParams] = useSearchParams();
  const error = searchParams.get('error');

  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const handleLoginGithub = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-body">
              <h2 className="card-title text-center mb-4">Prijava</h2>
              {error && (
                <div className="alert alert-danger" role="alert">
                  Greška prilikom prijave. Molimo pokušajte ponovno.
                </div>
              )}
              <div className="d-grid gap-2">
                <button 
                  className="btn btn-outline-dark"
                  onClick={handleGoogleLogin}
                >
                  <i className="bi bi-google me-2"></i>
                  Prijavi se pomoću Google računa
                </button>
                <button
                    onClick={() => handleLoginGithub()}
                    className="w-full py-2 bg-gray-800 hover:bg-gray-900 text-white font-medium rounded-lg"
                  >
                  Prijavi se pomoću Github-a
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}