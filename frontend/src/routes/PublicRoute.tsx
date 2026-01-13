import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../context/AuthContext";

function PublicRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();

  // Ako je korisnik već autentificiran, preusmjeri na /home
  if (isAuthenticated) {
    return <Navigate to="/home" replace />;
  }

  return children;
}

export default PublicRoute;
