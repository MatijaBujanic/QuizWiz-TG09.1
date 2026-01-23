import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/users": "https://quizwiz-tg091-production-504c.up.railway.app",
      "/api": "https://quizwiz-tg091-production-504c.up.railway.app",
      "/home": "https://quizwiz-tg091-production-504c.up.railway.app",
    },
  },
});
