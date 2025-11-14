import { useAuth } from "../context/AuthContext";
import React, { useState } from "react";

function AdminPage() {
  const { token } = useAuth();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage(null);
    setLoading(true);

    try {
      const response = await fetch(
        "https://quizwiz-tg091-production.up.railway.app/api/admin/createuser",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ username, email }),
        }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || "Failed to create user");
      }

      setMessage("Korisnik uspješno dodan!");
      setUsername("");
      setEmail("");
    } catch (err: any) {
      setMessage("Greška: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: "500px" }}>
    <h2 className="mb-4">Admin Panel - Dodaj korisnika</h2>

    <form onSubmit={handleSubmit} className="card p-4 shadow-sm">
    <div className="mb-3">
    <label className="form-label">Username</label>
    <input
    type="text"
    className="form-control"
    value={username}
    onChange={(e) => setUsername(e.target.value)}
    required
    minLength={3}
    />
    </div>

    <div className="mb-3">
    <label className="form-label">Email</label>
    <input
    type="email"
    className="form-control"
    value={email}
    onChange={(e) => setEmail(e.target.value)}
    required
    />
    </div>

    <button
    type="submit"
    className="btn btn-primary w-100"
    disabled={loading}
    >
    {loading ? "Dodavanje..." : "Dodaj korisnika"}
    </button>
    </form>

    {message && (
      <div
      className={`alert mt-3 text-center ${
        message.includes("Greška") ? "alert-danger" : "alert-success"
      }`}
      >
      {message}
      </div>
    )}
    </div>
  );
}

export default AdminPage;
