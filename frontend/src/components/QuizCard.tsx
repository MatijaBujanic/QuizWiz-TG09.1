import { type Quiz } from "../types/Quiz";

type Props = {
  quiz: Quiz;
};

export default function QuizCard({ quiz }: Props) {
  return (
    <div className="card mb-3">
      <div className="card-body">
        <h5 className="card-title">{quiz.title}</h5>
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
