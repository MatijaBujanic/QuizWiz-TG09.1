import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function OAuth2Callback() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  useEffect(() => {
    // Check if there's an error
    if (location.search.includes('error')) {
      navigate('/login');
      return;
    }

    // After successful OAuth2 login, we'll get a session cookie
    // We can consider the user logged in
    login('authenticated');
    navigate('/dashboard');
  }, [location, login, navigate]);

  return (
    <div className="container text-center mt-5">
      <div className="spinner-border" role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      <p className="mt-3">Prijava u tijeku...</p>
    </div>
  );
}