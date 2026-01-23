import axios from "axios";

export default axios.create({
  baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
  withCredentials: true,
  headers: {
    Accept: "application/json",
  },

  headers: {
    "Accept": "application/json",
    "Content-Type": "application/json",
    "Access-Control-Allow-Credentials": "true",
  },
});
