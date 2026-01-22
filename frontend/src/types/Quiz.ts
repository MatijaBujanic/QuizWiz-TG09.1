export type Quiz = {
  date: string;
  time: {
    hour: number;
    minute: number;
    second: number;
    nano: number;
  };
  description: string;
  status: string;
  quiz_id: number;
  quiz_name: string;
  quiz_theme: string;
  application_type: string;
  number_of_rounds: number;
  max_points: number;
  created_at: string;
  organizer_id: number;
  location_id: number;
  average_rating: number | null;
  rating_count: number;
};
