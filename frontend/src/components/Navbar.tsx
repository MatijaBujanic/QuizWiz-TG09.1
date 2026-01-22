import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { isAuthenticated, role, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const brandTo = isAuthenticated ? "/home" : "/";

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `nav-link ${isActive ? "active fw-semibold" : ""}`;

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <NavLink
          className="navbar-brand fw-bold"
          to={brandTo}
          style={{ fontSize: "1.35rem", letterSpacing: "0.5px" }}
        >
          QuizWiz <span className="text-warning">🍻</span>
        </NavLink>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto align-items-lg-center gap-lg-2">
            <li className="nav-item">
              <NavLink className={linkClass} to="/about">
                O aplikaciji
              </NavLink>
            </li>

            {role === "admin" && (
              <li className="nav-item">
                <NavLink className={linkClass} to="/admin">
                  Admin
                </NavLink>
              </li>
            )}
            {(role?.toUpperCase() === "ORGANIZER" ||
              role?.toUpperCase() === "ADMIN") && (
              <>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/create-quiz">
                    Create Quiz
                  </NavLink>
                </li>
                <li className="nav-item">
                  <NavLink className="nav-link" to="/my-quizzes">
                    My Quizzes
                  </NavLink>
                </li>
              </>
            )}

            {isAuthenticated && (
              <li className="nav-item dropdown">
                <button
                  className="nav-link dropdown-toggle"
                  id="featuresDropdown"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                  type="button"
                >
                  Izbornik
                </button>

                <ul
                  className="dropdown-menu dropdown-menu-end dropdown-menu-dark"
                  aria-labelledby="featuresDropdown"
                >
                  <li>
                    <NavLink className={linkClass} to="/quizzes">
                      Kvizovi
                    </NavLink>
                  </li>
                  <li>
                    <NavLink className={linkClass} to="/my-teams">
                      Moji timovi
                    </NavLink>
                  </li>
                  <li>
                    <hr className="dropdown-divider" />
                  </li>

                  <li>
                    <button
                      className="dropdown-item text-danger"
                      onClick={handleLogout}
                      type="button"
                    >
                      Odjava
                    </button>
                  </li>
                </ul>
              </li>
            )}
            {!isAuthenticated ? (
              <li className="nav-item">
                <NavLink className={linkClass} to="/login">
                  Prijava
                </NavLink>
              </li>
            ) : null}
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
