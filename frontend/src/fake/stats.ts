import { type Stats } from "../types/Stats";

export const defaultStats: Stats = {
  played: 3,
  totalPoints: 74,
  avgPosition: 2.3,
  history: [
    {
      id: "h1",
      quizTitle: "Filmski kviz",
      points: 28,
      position: 2,
      date: "2025-12-10",
    },
    {
      id: "h2",
      quizTitle: "Opći kviz znanja",
      points: 24,
      position: 3,
      date: "2025-12-17",
    },
    {
      id: "h3",
      quizTitle: "Glazbeni kviz",
      points: 22,
      position: 2,
      date: "2025-12-22",
    },
  ],
}