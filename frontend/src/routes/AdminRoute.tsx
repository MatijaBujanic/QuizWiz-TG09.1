import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../context/AuthContext";

function AdminRoute({ children }: { children: ReactNode }) {
  const { role, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (role != "admin") {
    return <Navigate to="/home" replace />;
  }
  return children;
}

export default AdminRoute;
