import { useMemo, useState } from "react";
import { loadFromStorage, saveToStorage } from "../utils/storage";
import type { TeamProfile } from "../types/Team";

const emptyProfile: TeamProfile = {
  teamName: "Moj tim",
  contactNumber: "",
  email: "",
  members: [],
};

export default function MyTeamPage() {
  const initial = useMemo(
    () => loadFromStorage<TeamProfile>("team_profile", emptyProfile),
    []
  );

  const [profile, setProfile] = useState<TeamProfile>(initial);
  const [editing, setEditing] = useState(false);
  const [newMember, setNewMember] = useState("");

  const persist = (next: TeamProfile) => {
    setProfile(next);
    saveToStorage("team_profile", next);
  };

  const handleRemoveMember = (id: string) => {
    const next = {
      ...profile,
      members: profile.members.filter((m) => m.id !== id),
    };
    persist(next);
  };

  const handleAddMember = () => {
    const name = newMember.trim();
    if (!name) return;

    const next = {
      ...profile,
      members: [...profile.members, { id: crypto.randomUUID(), name }],
    };
    persist(next);
    setNewMember("");
  };

  return (
    <div className="container my-5" style={{ maxWidth: 900 }}>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h2 className="mb-0">Profil tima</h2>
        <button
          className={`btn ${editing ? "btn-success" : "btn-outline-primary"}`}
          onClick={() => setEditing((v) => !v)}
        >
          {editing ? "Završi uređivanje" : "Uredi profil"}
        </button>
      </div>

      <div className="row g-3">
        {/* Profile card */}
        <div className="col-md-6">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h5 className="card-title">Osnovni podaci</h5>

              <div className="mb-3">
                <label className="form-label">Naziv tima</label>
                <input
                  className="form-control"
                  value={profile.teamName}
                  disabled={!editing}
                  onChange={(e) =>
                    persist({ ...profile, teamName: e.target.value })
                  }
                />
              </div>

              <div className="mb-3">
                <label className="form-label">E-mail</label>
                <input
                  className="form-control"
                  value={profile.email}
                  disabled={!editing}
                  onChange={(e) =>
                    persist({ ...profile, email: e.target.value })
                  }
                />
              </div>

              <div className="mb-0">
                <label className="form-label">Kontakt broj</label>
                <input
                  className="form-control"
                  value={profile.contactNumber}
                  disabled={!editing}
                  onChange={(e) =>
                    persist({ ...profile, contactNumber: e.target.value })
                  }
                />
              </div>

              <div className="text-muted small mt-3">
                *Podaci se spremaju lokalno (localStorage).
              </div>
            </div>
          </div>
        </div>

        {/* Members card */}
        <div className="col-md-6">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h5 className="card-title d-flex justify-content-between align-items-center">
                Članovi tima
                <span className="badge text-bg-secondary">
                  {profile.members.length}
                </span>
              </h5>

              <ul className="list-group mb-3">
                {profile.members.length === 0 ? (
                  <li className="list-group-item text-muted">
                    Još nema članova.
                  </li>
                ) : (
                  profile.members.map((m) => (
                    <li
                      key={m.id}
                      className="list-group-item d-flex justify-content-between align-items-center"
                    >
                      <span>{m.name}</span>
                      {editing && (
                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => handleRemoveMember(m.id)}
                        >
                          Ukloni
                        </button>
                      )}
                    </li>
                  ))
                )}
              </ul>

              {editing && (
                <div className="input-group">
                  <input
                    className="form-control"
                    value={newMember}
                    onChange={(e) => setNewMember(e.target.value)}
                    placeholder="Ime člana"
                  />
                  <button className="btn btn-primary" onClick={handleAddMember}>
                    Dodaj
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
