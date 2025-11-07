import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';

interface AuthContextType {
  isAuthenticated: boolean;
  login: (status: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  const login = () => {
    setIsAuthenticated(true);
  };

  const logout = async () => {
    try {
      // Call backend logout endpoint
      await fetch('http://localhost:8080/logout', {
        method: 'POST',
        credentials: 'include'
      });
    } finally {
      setIsAuthenticated(false);
      window.location.href = '/';
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
