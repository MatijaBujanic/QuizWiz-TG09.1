import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../api/api";

export default function QuizDetailsPage() {
  const { id } = useParams();
  const [quiz, setQuiz] = useState<any>(null);

  useEffect(() => {
    api
      .get(`/api/quizzes/${id}`)
      .then((res) => setQuiz(res.data))
      .catch(console.error);
  }, [id]);

  if (!quiz) return <p>Loading...</p>;

  return (
    <div className="container mt-4">
      <h2>{quiz.quiz_name}</h2>
      <p>Tema: {quiz.quiz_theme}</p>
      <p>Datum: {quiz.date}</p>
      <p>Status: {quiz.status}</p>
    </div>
  );
}
