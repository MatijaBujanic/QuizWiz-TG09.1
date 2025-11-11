import { Link } from "react-router-dom";

const HeroSection = () => {
  return (
    <div className="bg-primary text-white text-center py-5">
      <div className="container">
        <h1 className="display-4 fw-bold">Dobrodošli u QuizWiz 🎉</h1>
        <p className="lead mt-3 mb-4">
          Aplikacija za jednostavnu organizaciju pub kvizova, timova i pitanja.
        </p>
        <Link to="/login" className="btn btn-light btn-lg">
          Započni
        </Link>
      </div>
    </div>
  );
};

export default HeroSection;
