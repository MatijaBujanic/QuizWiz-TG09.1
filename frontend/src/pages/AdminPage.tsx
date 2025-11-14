import { useAuth } from "../context/AuthContext";
import React, { useEffect, useState } from "react";

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
}

function AdminPage() {
  const { token } = useAuth();

  const [users, setUsers] = useState<User[]>([]);
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchUsers = async () => {
    try {
      const response = await fetch(
        "https://quizwiz-tg091-production.up.railway.app/api/admin/users",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        console.error("Failed to fetch users: " + response.status);
        return;
      }

      const data = await response.json();
      setUsers(data.users);
    } catch (err) {
      console.error("Error fetching users: ", err);
    }
  };

  const handleAddUser = async (e: React.FormEvent) => {
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
        console.error("Failed to add user. Status:", response.status);
        return;
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

  const handleDelete = async (userEmail: string) => {
    if (!confirm("Jeste li sigurni?")) return;

    try {
      console.log("Deleting user with email:", userEmail);
      console.log("Current token:", token);

      const response = await fetch(
        `https://quizwiz-tg091-production.up.railway.app/api/admin/deleteuser`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ email: userEmail }),
        }
      );

      console.log("Response status:", response.status);
      console.log("Response ok:", response.ok);

      const responseText = await response.text();
      console.log("Response text:", responseText);

      let responseData;
      try {
        responseData = JSON.parse(responseText);
        console.log("Parsed response:", responseData);
      } catch (e) {
        console.log("Could not parse response as JSON");
      }

      if (!response.ok) {
        console.error("Failed to delete user. Status:", response.status);
        if (responseData?.message) {
          console.error("Error message:", responseData.message);
          alert("Error: " + responseData.message);
        }
        return;
      }

      console.log("User deleted successfully");
      if (responseData?.message) {
        alert(responseData.message);
      }
      fetchUsers();
    } catch (err) {
      console.error("Error deleting user:", err);
      alert("Error deleting user. Check console for details.");
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <div className="container mt-5" style={{ maxWidth: "500px" }}>
      <h2 className="mb-4">Admin Panel</h2>

      <div className="card mb-4">
        <div className="card-body">
          <h4 className="card-title mb-3">Dodaj korisnika</h4>

          <form onSubmit={handleAddUser}>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                type="text"
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
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

            <button className="btn btn-primary" type="submit">
              Dodaj
            </button>
          </form>
        </div>
        {message && (
          <div className="alert alert-info mt-3 text-center">{message}</div>
        )}
      </div>

      <h4 className="mb-3">Korisnici</h4>

      <table className="table table-striped table-bordered">
        <thead className="table-dark">
          <tr>
            <th>Email</th>
            <th>Username</th>
            <th>Role</th>
            <th style={{ width: "120px" }}>Actions</th>
          </tr>
        </thead>

        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.email}</td>
              <td>{u.username}</td>
              <td>{u.role}</td>
              <td>
                {u.role === "admin" ? (
                  <span className="text-muted">Admin</span>
                ) : (
                  <button
                    className="btn btn-sm btn-danger d-flex justify-content-center align-items-center"
                    style={{ width: "32px", height: "32px" }}
                    onClick={() => handleDelete(u.email)}
                  >
                    <i className="bi bi-trash me-1"></i>
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminPage;
