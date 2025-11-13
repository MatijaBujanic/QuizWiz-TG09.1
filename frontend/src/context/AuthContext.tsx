import { jwtDecode } from "jwt-decode";
import { createContext, useContext, useState, useEffect } from "react";
import type { ReactNode } from "react";

interface DecodedToken {
  sub: string;
  role?: string;
  exp: number;
}

interface AuthContextType {
  isAuthenticated: boolean;
  token: string | null;
  role: string | null;
  login: (token: string) => void;
  logout: () => void;
}

const [role, setRole] = useState<string | null>(null);

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem("jwt_token")
  );
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!token);

  useEffect(() => {
    if (token) {
      localStorage.setItem("jwt_token", token);
      setIsAuthenticated(true);
    } else {
      localStorage.removeItem("jwt_token");
      setIsAuthenticated(false);
    }
  }, [token]);

  const login = (newToken: string) => {
    const decoded: DecodedToken = jwtDecode(newToken);
    setRole(decoded.role ?? null);
    setToken(newToken);
  };

  const logout = () => {
    setToken(null);
    setRole(null);
    window.location.href = "/";
  };

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, token, role, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
