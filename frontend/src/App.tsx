import AboutPage from "./pages/AboutPage";
import AdminPage from "./pages/AdminPage";
import AdminRoute from "./routes/AdminRoute";
import Footer from "./components/Footer";
import HomePage from "./pages/HomePage";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import Navbar from "./components/Navbar";
import OAuth2Callback from "./pages/OAuth2Callback";
import QuizzesPage from "./pages/QuizzesPage";
import QuizDetailsPage from "./pages/QuizDetailsPage";
import { Routes, Route } from "react-router-dom";
import OrganizerRoute from "./routes/OrganizerRoute";
import CreateQuizPage from "./pages/CreateQuizPage";
import MyQuizzesPage from "./pages/MyQuizzesPage";
import RegisterPage from "./pages/RegisterPage";
import MyTeamPage from "./pages/MyTeamPage";
import ProtectedRoute from "./routes/ProtectedRoute";

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Navbar />
      <div className="flex-grow-1">
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth2/success" element={<OAuth2Callback />} />
          <Route
            path="/home"
            element={
              <ProtectedRoute>
                <HomePage />
              </ProtectedRoute>
            }
          ></Route>
          <Route path="/quizzes" element={<QuizzesPage />}></Route>
          <Route path="/quizzes/:id" element={<QuizDetailsPage />} />
          <Route path="/about" element={<AboutPage />}></Route>
          <Route path="/register" element={<RegisterPage />}></Route>
          <Route path="/my-teams" element={<MyTeamPage />}></Route>
          <Route
            path="/create-quiz"
            element={
              <ProtectedRoute>
                <OrganizerRoute>
                  <CreateQuizPage />
                </OrganizerRoute>
              </ProtectedRoute>
            }
          ></Route>
          <Route
            path="/edit-quiz/:id"
            element={
              <ProtectedRoute>
                <OrganizerRoute>
                  <CreateQuizPage />
                </OrganizerRoute>
              </ProtectedRoute>
            }
          ></Route>
          <Route
            path="/my-quizzes"
            element={
              <ProtectedRoute>
                <OrganizerRoute>
                  <MyQuizzesPage />
                </OrganizerRoute>
              </ProtectedRoute>
            }
          ></Route>
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <AdminRoute>
                  <AdminPage />
                </AdminRoute>
              </ProtectedRoute>
            }
          ></Route>
        </Routes>
      </div>
      <Footer />
    </div>
  );
}

export default App;
