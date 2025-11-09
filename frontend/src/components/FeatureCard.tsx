interface FeatureCardProps {
  icon: string;
  title: string;
  text: string;
}

const FeatureCard = ({ icon, title, text }: FeatureCardProps) => {
  return (
    <div className="col-md-4 mb-4">
      <div className="card h-100 shadow-sm border-0 text-center">
        <div className="card-body">
          <i className={`bi ${icon} display-5 text-primary mb-3`}></i>
          <h5 className="card-title">{title}</h5>
          <p className="card-text text-muted">{text}</p>
        </div>
      </div>
    </div>
  );
};

export default FeatureCard;
