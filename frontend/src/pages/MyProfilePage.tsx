import { useEffect, useState } from "react";
import axios from "axios";

interface User {
  username: string;
  email: string;
  contact_number: string;
  role: string;
}

const MyProfilePage = () => {
  const [user, setUser] = useState<User | null>(null);
  const [username, setUsername] = useState("");
  const [contactNumber, setContactNumber] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axios.get<User>("/users/me");
        setUser(res.data);
        setUsername(res.data.username ?? "");
        setContactNumber(res.data.contact_number ?? "");
      } catch (err) {
        setError("Greška pri dohvaćanju profila.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setMessage(null);
    setError(null);

    try {
      await axios.patch("/users/me/edit", {
        username,
        contact_number: contactNumber,
      });

      setMessage("Profil uspješno ažuriran.");
    } catch (err) {
      setError("Greška pri spremanju profila.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="container mt-5">Učitavanje profila...</div>;
  }

  if (!user) {
    return (
      <div className="container mt-5 text-danger">Profil nije dostupan.</div>
    );
  }

  return (
    <div className="container mt-5" style={{ maxWidth: "600px" }}>
      <h2 className="mb-4">Moj profil</h2>

      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            type="email"
            className="form-control"
            value={user.email}
            disabled
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Korisničko ime</label>
          <input
            type="text"
            className="form-control"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Kontakt broj</label>
          <input
            type="text"
            className="form-control"
            value={contactNumber}
            onChange={(e) => setContactNumber(e.target.value)}
          />
        </div>

        {message && <div className="alert alert-success">{message}</div>}
        {error && <div className="alert alert-danger">{error}</div>}

        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? "Spremanje..." : "Spremi promjene"}
        </button>
      </form>
    </div>
  );
};

export default MyProfilePage;
