import AdminPage from "./pages/AdminPage";
import AdminRoute from "./routes/AdminRoute";
import Footer from "./components/Footer";
import HomePage from "./pages/HomePage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import Navbar from "./components/Navbar";
import OAuth2Callback from "./pages/OAuth2Callback";
import { Routes, Route } from "react-router-dom";

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Navbar />
      <div className="flex-grow-1">
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth2/success" element={<OAuth2Callback />} />
          <Route path="/home" element={<HomePage />}></Route>
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminPage />
              </AdminRoute>
            }
          ></Route>
        </Routes>
        <Footer />
      </div>
    </div>
  );
}

export default App;
