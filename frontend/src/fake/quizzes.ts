import { type Quiz } from "../types/Quiz";

export const quizzes: Quiz[] = [
  {
    id: 1,
    title: "Opći kviz znanja",
    description: "Kviz za sve koji misle da znaju sve",
    date: "2026-01-20 19:00",
    location: "Zagreb",
    maxTeams: 20,
    registeredTeams: 12,
  },
  {
    id: 2,
    title: "Napredni React",
    description: "Napredniji React kviz za iskusnije programere",
    date: "2026-02-15 18:30",
    location: "Rijeka",
    maxTeams: 8,
    registeredTeams: 8,
  },
  {
    id: 3,
    title: "Algoritmi i strukture podataka",
    description: "Kviz o algoritmima i strukturama podataka",
    date: "2026-03-10 17:00", 
    location: "Split",
    maxTeams: 12,
    registeredTeams: 6,
  },
  {
    id: 4,
    title: "Baze podataka - praktični dio",
    description: "Praktični dio kviza o bazama podataka",
    date: "2026-01-01 16:00",
    location: "Zagreb",
    maxTeams: 6,
    registeredTeams: 6,
  },
  {
    id: 5,
    title: "Web sigurnost",
    description: "Kviz o web sigurnosti i zaštiti podataka",
    date: "2026-04-05 19:30",
    location: "Zadar",
    maxTeams: 5,
    registeredTeams: 2,
  },
];
