export default function CustomLoginButton() {
  const handleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <button className="btn btn-primary" onClick={handleLogin}>
      Prijava s Google (custom)
    </button>
  );
}