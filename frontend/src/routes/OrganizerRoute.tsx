import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../context/AuthContext";

function OrganizerRoute({ children }: { children: ReactNode }) {
  const { role, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (role !== "organizer" && role !== "admin") {
    return <Navigate to="/home" replace />;
  }

  return children;
}

export default OrganizerRoute;
