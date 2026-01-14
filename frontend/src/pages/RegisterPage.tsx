import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { saveToStorage } from "../utils/storage";
import type { TeamProfile } from "../types/Team";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [teamName, setTeamName] = useState("");
  const [contactNumber, setContactNumber] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Fake register: store team profile locally
    const profile: TeamProfile = {
      teamName,
      contactNumber,
      email,
      members: [{ id: crypto.randomUUID(), name: username }],
    };

    saveToStorage("team_profile", profile);

    // optional: also store "registered user" info
    saveToStorage("registered_user", { username, email });

    navigate("/my-team");
  };

  return (
    <div className="container my-5" style={{ maxWidth: 720 }}>
      <h2 className="mb-4">Registracija</h2>

      <div className="card shadow-sm">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="row g-3">
              <div className="col-md-6">
                <label className="form-label">Korisničko ime</label>
                <input
                  className="form-control"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>

              <div className="col-md-6">
                <label className="form-label">Lozinka</label>
                <input
                  type="password"
                  className="form-control"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>

              <div className="col-md-6">
                <label className="form-label">E-mail</label>
                <input
                  type="email"
                  className="form-control"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>

              <div className="col-md-6">
                <label className="form-label">Kontakt broj</label>
                <input
                  className="form-control"
                  value={contactNumber}
                  onChange={(e) => setContactNumber(e.target.value)}
                  placeholder="+385..."
                  required
                />
              </div>

              <div className="col-12">
                <label className="form-label">Naziv tima</label>
                <input
                  className="form-control"
                  value={teamName}
                  onChange={(e) => setTeamName(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="d-flex gap-2 mt-4">
              <button className="btn btn-primary" type="submit">
                Registriraj se
              </button>
              <button
                className="btn btn-outline-secondary"
                type="button"
                onClick={() => navigate("/")}
              >
                Odustani
              </button>
            </div>

            <div className="text-muted small mt-3">
              *Ovo je trenutno “fake” registracija (sprema se lokalno).
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
