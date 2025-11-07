import "./App.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import HeroSection from "./components/HeroSection";
import FeatureCard from "./components/FeatureCard";
import Footer from "./components/Footer";
import Login from "./pages/Login";
import OAuth2Callback from "./pages/OAuth2Callback";

function HomePage() {
  return (
    <>
      <HeroSection />
      <div className="container mt-5 mb-5">
        <h2 className="text-center mb-4">Što nudi QuizWiz?</h2>
        <div className="row">
          <FeatureCard
            title="Jednostavno kreiranje kvizova"
            text="Kreiraj i uređuj kvizove u par klikova uz intuitivno sučelje."
            icon="bi-pencil-square"
          />
          <FeatureCard
            title="Praćenje rezultata"
            text="Automatski prikaži rezultate i statistiku svakog tima."
            icon="bi-bar-chart-line"
          />
          <FeatureCard
            title="Organizacija događaja"
            text="Planiraj pub kvizove, dodaj timove i vodi cijeli event iz aplikacije."
            icon="bi-calendar-event"
          />
        </div>
      </div>
    </>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="d-flex flex-column min-vh-100">
          <Navbar />
          <main className="flex-grow-1">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/oauth2/success" element={<OAuth2Callback />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
