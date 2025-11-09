import FeatureCard from "../components/FeatureCard";
import HeroSection from "../components/HeroSection";

const LandingPage = () => {
  const features = [
    {
      icon: "bi-pencil-square",
      title: "Jednostavno kreiranje kvizova",
      text: "Kreiraj i uređuj kvizove u par klikova uz intuitivno sučelje.",
    },
    {
      icon: "bi-bar-chart-line",
      title: "Praćenje rezultata",
      text: "Automatski prikaži rezultate i statistiku svakog tima.",
    },
    {
      icon: "bi-calendar-event",
      title: "Organizacija događaja",
      text: "Planiraj pub kvizove, dodaj timove i vodi cijeli event iz aplikacije.",
    },
  ];

  return (
    <>
      <HeroSection />
      <div className="container my-5">
        <div className="row">
          {features.map((feature, index) => (
            <FeatureCard
              key={index}
              icon={feature.icon}
              title={feature.title}
              text={feature.text}
            />
          ))}
        </div>
      </div>
    </>
  );
};

export default LandingPage;
