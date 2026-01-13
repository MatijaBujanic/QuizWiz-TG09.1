import QuizCard from "../components/QuizCard";
import { quizzes } from "../fake/quizzes"; // const response = await fetch("/api/quizzes");

export default function QuizzesPage() {
  return (
    <div className="container mt-4">
      <h2>Dostupni kvizovi</h2>
      {quizzes.map((quiz) => (
        <QuizCard key={quiz.id} quiz={quiz} />
      ))}
    </div>
  );
}
