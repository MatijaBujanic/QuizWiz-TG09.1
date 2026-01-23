import axios from "axios";

export default axios.create({
  baseURL: "quizwiz-tg091-production-504c.up.railway.app",
  withCredentials: true,
  headers: {
    Accept: "application/json",
  },
});
