import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/users": "quizwiz-tg091-production-504c.up.railway.app",
      "/api": "quizwiz-tg091-production-504c.up.railway.app",
      "/home": "quizwiz-tg091-production-504c.up.railway.app",
    },
  },
});
