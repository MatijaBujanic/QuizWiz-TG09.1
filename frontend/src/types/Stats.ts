type HistoryItem = {
  id: string;
  quizTitle: string;
  points: number;
  position: number;
  date: string;
};

export type Stats = {
  played: number;
  totalPoints: number;
  avgPosition: number;
  history: HistoryItem[];
};