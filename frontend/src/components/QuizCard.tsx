import { type Quiz } from "../types/Quiz";

type Props = {
  quiz: Quiz;
};

export default function QuizCard({ quiz }: Props) {
  const isOpen = quiz.registeredTeams < quiz.maxTeams;

  return (
    <div className="card mb-3">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <h5 className="card-title mb-0">{quiz.title}</h5>
          <span className={"badge " + (isOpen ? "bg-success" : "bg-secondary")}>
            {isOpen ? "Otvorene prijave" : "Popunjeno"}
          </span>
        </div>
        <p className="card-text">{quiz.description}</p>
        <p className="text-muted">
          📍 {quiz.location} | 🕒 {quiz.date}
        </p>
        <p>
          Timovi: {quiz.registeredTeams}/{quiz.maxTeams}
        </p>
        <button className="btn btn-primary">Detalji</button>
      </div>
    </div>
  );
}
