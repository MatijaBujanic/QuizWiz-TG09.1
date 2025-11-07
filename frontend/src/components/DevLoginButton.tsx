import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function DevLoginButton() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleDevLogin = () => {
    // Temporary dev token — replace with real token after backend is fixed
    const devToken = 'dev-token-please-replace';
    login(devToken);
    navigate('/dashboard');
  };

  return (
    <button className="btn btn-sm btn-outline-secondary" onClick={handleDevLogin}>
      Dev login (no backend)
    </button>
  );
}
