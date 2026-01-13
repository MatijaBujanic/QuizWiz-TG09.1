type ApplicationStatus = "Prijavljen" | "Potvrđen" | "Odbijen" | "Otkazano";

export type Application = {
  id: string;
  quizTitle: string;
  date: string;
  location: string;
  status: ApplicationStatus;
};