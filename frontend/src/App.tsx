import "./App.css";

import "bootstrap/dist/css/bootstrap.min.css";
import Navbar from "./components/NavBar";
import HeroSection from "./components/HeroSection";
import FeatureCard from "./components/FeatureCard";
import Footer from "./components/Footer";

function App() {
  return (
    <>
      <Navbar />
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

      <Footer />
    </>
  );
}

export default App;
