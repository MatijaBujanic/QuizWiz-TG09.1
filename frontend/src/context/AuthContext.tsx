import { createContext, useContext, useState, useEffect } from "react";
import type { ReactNode } from "react";

interface AuthContextType {
  isAuthenticated: boolean;
  token: string | null;
  role: string | null;
  login: (token: string) => void;
  logout: () => void;
}

const [role, setRole] = useState<string | null>(() =>
  localStorage.getItem("role")
);

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

  const login = async (newToken: string) => {
    setToken(newToken);

    try {
      const response = await fetch(
        "https://quizwiz-tg091-production.up.railway.app/api/admin/user", // change link if necessary
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        setRole(data.role);
        localStorage.setItem("role", data.role);
      } else {
        console.error("Failed to fetch role");
      }
    } catch (err) {
      console.error("Error fetching user role", err);
    }
  };

  const logout = () => {
    setToken(null);
    setRole(null);
    localStorage.removeItem("role");
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
