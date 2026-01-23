import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { useParams } from "react-router-dom";
import GoogleMap from "../components/GoogleMap";
import axios from "axios";

// Kreiraj axios instancu s interceptorom
const axiosInstance = axios.create({
  baseURL: "http://quizwiz-tg091-production-504c.up.railway.app",
  withCredentials: true, // Šalji cookies (session) automatski
});

// Response interceptor - detektuj HTML odgovore
axiosInstance.interceptors.response.use(
  (response) => {
    // Provjeri je li response HTML (login stranica)
    if (typeof response.data === 'string' && response.data.includes('<!DOCTYPE')) {
      console.error("Backend vraća login HTML umjesto JSON-a!");
      console.error("Response status:", response.status);
      console.error("Session možda nije validna - trebam OAuth2 login");
      throw new Error("Sesija nije validna - molimo prijavite se ponovno");
    }
    return response;
  },
  (error) => {
    console.error("Axios error:", error);
    if (error.response?.status === 401 || error.response?.status === 403) {
      console.error("401/403 - redirekcija na login");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default function CreateQuizPage() {
  const { token } = useAuth();
  const { id } = useParams(); // Ako postoji ID, onda je edit mode
  const isEditMode = Boolean(id);
  
  const [selectedLocation, setSelectedLocation] = useState<{
    lat: number;
    lng: number;
  } | null>(null);
  const [address, setAddress] = useState<string | null>(null);
  const [locationName, setLocationName] = useState<string>("");
  const [city, setCity] = useState<string>("");
  const [capacity, setCapacity] = useState<number>(0);
  const [isFetchingAddress, setIsFetchingAddress] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Quiz fields
  const [quizName, setQuizName] = useState<string>("");
  const [quizTheme, setQuizTheme] = useState<string>("");
  const [applicationType, setApplicationType] = useState<string>("team");
  const [quizDate, setQuizDate] = useState<string>("");
  const [quizTime, setQuizTime] = useState<string>("");
  const [description, setDescription] = useState<string>("");
  const [numberOfRounds, setNumberOfRounds] = useState<number>(1);
  const [maxPoints, setMaxPoints] = useState<number>(100);
  const [locationId, setLocationId] = useState<number | null>(null);
  const [status, setStatus] = useState<string>("open");

  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

  // Load quiz data if in edit mode
  useEffect(() => {
    if (!isEditMode || !id) return;

    const loadQuiz = async () => {
      try {
        setLoading(true);
        const quizRes = await axiosInstance.get(`/api/quizzes/${id}`);
        const quiz = quizRes.data;
        
        setQuizName(quiz.quiz_name || "");
        setQuizTheme(quiz.quiz_theme || "");
        setApplicationType(quiz.application_type || "team");
        setQuizDate(quiz.date || "");
        setQuizTime(typeof quiz.time === 'string' ? quiz.time.slice(0, 5) : "");
        setDescription(quiz.description || "");
        setNumberOfRounds(quiz.number_of_rounds || 1);
        setMaxPoints(quiz.max_points || 100);
        setLocationId(quiz.location_id || null);
        setStatus(quiz.status || "open");

        // Load location if exists
        if (quiz.location_id) {
          try {
            const locRes = await axiosInstance.get(`/api/locations/${quiz.location_id}`);
            const loc = locRes.data;
            setLocationName(loc.location_name || "");
            setAddress(loc.address || "");
            setCity(loc.city || "");
            setCapacity(loc.capacity || 0);
          } catch (locErr) {
            console.error('Location load error:', locErr);
          }
        }
      } catch (err) {
        console.error('Quiz load error:', err);
        setError('Greška pri učitavanju kviza');
      } finally {
        setLoading(false);
      }
    };

    loadQuiz();
  }, [id, isEditMode]);

  if (!apiKey) {
    return <div>Greška: Google Maps API ključ nije postavljen.</div>; // Fallback ako ključ nedostaje
  }

  const handleLocationSelect = (location: { lat: number; lng: number }) => {
    setSelectedLocation(location);
    fetchAddress(location.lat, location.lng);
  };

  const fetchAddress = async (lat: number, lng: number) => {
    setIsFetchingAddress(true);
    try {
      const response = await fetch(
        `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=${apiKey}&language=hr&region=hr`
      );
      const data = await response.json();
      if (data.results && data.results.length > 0) {
        // Pronađi najbolju adresu - preferira street_address tip
        const streetAddress = data.results.find((r: any) => r.types.includes('street_address'));
        const result = streetAddress || data.results[0];
        
        // Koristi formatted_address ali očisti duplicirane gradove
        let formattedAddr = result.formatted_address;
        
        // Automatski popuni grad iz address_components
        const cityComponent = result.address_components?.find((comp: any) => 
          comp.types.includes('locality') || comp.types.includes('postal_town')
        );
        if (cityComponent && !city) {
          setCity(cityComponent.long_name);
        }
        
        setAddress(formattedAddr);
      } else {
        setAddress("Adresa nije pronađena");
      }
    } catch (error) {
      console.error("Greška prilikom dohvaćanja adrese:", error);
      setAddress("Greška prilikom dohvaćanja adrese");
    } finally {
      setIsFetchingAddress(false);
    }
  };

  const handleSubmit = async () => {
    if (!selectedLocation || !locationName || !city || capacity <= 0) {
      setError("Molimo popunite sve podatke o lokaciji.");
      return;
    }
    if (!quizName || !quizTheme || !quizDate || !quizTime) {
      setError("Molimo popunite sve potrebne podatke o kvizu.");
      return;
    }

    // Debug: provjeri token
    if (!token) {
      setError("Token nije pronađen. Molimo prijavite se ponovno.");
      console.error("Token je null ili undefined");
      return;
    }
    console.log("Token:", token?.substring(0, 50) + "...");

    setLoading(true);
    setError(null);

    try {
      if (isEditMode && id) {
        // EDIT MODE - update existing quiz
        console.log("Ažuriram kviz ID:", id);
        
        const updateData: any = {
          quiz_name: quizName,
          quiz_theme: quizTheme,
          application_type: applicationType,
          date: quizDate,
          time: quizTime,
          description: description,
          number_of_rounds: numberOfRounds,
          max_points: maxPoints,
          status: status,
        };

        // Only include location_id if we have one
        if (locationId) {
          updateData.location_id = locationId;
        }

        console.log("Šaljem update podatke:", updateData);

        const quizResponse = await axiosInstance.patch(
          `/api/organizer/quizzes/${id}`,
          updateData
        );

        console.log("Kviz ažuriran:", quizResponse.data);
        alert("Kviz je uspješno ažuriran!");
        
        // Redirect to my quizzes
        window.location.href = "/my-quizzes";
        return;
      }

      // CREATE MODE - existing logic
      // First, create location if needed
      let finalLocationId = locationId;
      if (!locationId) {
        console.log("Kreiram lokaciju...");
        
        const locationResponse = await axiosInstance.post(
          "/api/organizer/locations",
          {
            location_name: locationName,
            address: address,
            capacity: capacity,
            city: city,
          }
        );
        console.log("Location response:", locationResponse.data);
        // Pokušaj dohvatiti locationId iz različitih mogućih ključeva
        finalLocationId = locationResponse.data?.location_id || 
                          locationResponse.data?.locationId || 
                          locationResponse.data?.id;
        setLocationId(finalLocationId);
        console.log("Lokacija kreirana s ID:", finalLocationId);
      }

      // Then create quiz
      console.log("Kreiram kviz...");
      const quizResponse = await axiosInstance.post(
        "/api/organizer/quizzes",
        {
          quiz_name: quizName,
          quiz_theme: quizTheme,
          application_type: applicationType,
          date: quizDate,
          time: quizTime,
          description: description,
          number_of_rounds: numberOfRounds,
          max_points: maxPoints,
          location_id: finalLocationId,
        }
      );

      console.log("Kviz response:", quizResponse.data);
      console.log("Kviz uspješno kreiran:", quizResponse.data);
      
      // Spremi lokalno za fallback u MyQuizzes
      try {
        const localQuizzesRaw = localStorage.getItem('local_created_quizzes');
        const localQuizzes = localQuizzesRaw ? JSON.parse(localQuizzesRaw) : [];
        localQuizzes.push({
          ...quizResponse.data,
          created_at: new Date().toISOString(),
        });
        localStorage.setItem('local_created_quizzes', JSON.stringify(localQuizzes));
      } catch (storageErr) {
        console.warn('Could not save quiz to localStorage', storageErr);
      }
      
      alert("Kviz i lokacija su uspješno kreirani!");
      
      // Reset form
      setQuizName("");
      setQuizTheme("");
      setApplicationType("team");
      setQuizDate("");
      setQuizTime("");
      setDescription("");
      setNumberOfRounds(1);
      setMaxPoints(100);
      setLocationName("");
      setCity("");
      setCapacity(0);
      setLocationId(null);
    } catch (err: any) {
      console.error("Greška pri stvaranju kviza:", err);
      console.error("Error message:", err.message);
      console.error("Response status:", err.response?.status);
      console.error("Response data:", err.response?.data);
      
      if (err.message?.includes("Sesija") || err.message?.includes("login")) {
        setError("Molimo prijavite se ponovno preko OAuth2 (GitHub/Google)");
      } else {
        setError(err.message || err.response?.data?.message || "Greška pri stvaranju kviza. Molimo pokušajte ponovno.");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const existingQuizLocation = { lat: 45.8150, lng: 15.9819 }; // Example: Zagreb
    setSelectedLocation(existingQuizLocation);
    fetchAddress(existingQuizLocation.lat, existingQuizLocation.lng);
  }, []);

  return (
    <div className="container mt-4">
      <h2>{isEditMode ? "Uredi kviz" : "Kreiraj novi kviz"}</h2>
      
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row">
        <div className="col-md-6">
          <h4>Podaci o kvizu</h4>
          
          <div className="mb-3">
            <label>Naziv kviza*</label>
            <input
              type="text"
              className="form-control"
              value={quizName}
              onChange={(e) => setQuizName(e.target.value)}
              placeholder="npr. Opća znanja"
            />
          </div>

          <div className="mb-3">
            <label>Tema kviza*</label>
            <input
              type="text"
              className="form-control"
              value={quizTheme}
              onChange={(e) => setQuizTheme(e.target.value)}
              placeholder="npr. Povijest"
            />
          </div>

          <div className="mb-3">
            <label>Vrsta natjecanja</label>
            <select
              className="form-control"
              value={applicationType}
              onChange={(e) => setApplicationType(e.target.value)}
            >
              <option value="team">Tim</option>
              <option value="individual">Pojedinac</option>
            </select>
          </div>

          <div className="mb-3">
            <label>Datum*</label>
            <input
              type="date"
              className="form-control"
              value={quizDate}
              onChange={(e) => setQuizDate(e.target.value)}
            />
          </div>

          <div className="mb-3">
            <label>Vrijeme*</label>
            <input
              type="time"
              className="form-control"
              value={quizTime}
              onChange={(e) => setQuizTime(e.target.value)}
            />
          </div>

          <div className="mb-3">
            <label>Opis</label>
            <textarea
              className="form-control"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              placeholder="Detaljniji opis kviza"
            ></textarea>
          </div>

          <div className="mb-3">
            <label>Broj rundi</label>
            <input
              type="number"
              className="form-control"
              value={numberOfRounds}
              onChange={(e) => setNumberOfRounds(Number(e.target.value))}
              min="1"
            />
          </div>

          <div className="mb-3">
            <label>Maksimalni bodovi</label>
            <input
              type="number"
              className="form-control"
              value={maxPoints}
              onChange={(e) => setMaxPoints(Number(e.target.value))}
              min="1"
            />
          </div>

          {isEditMode && (
            <div className="mb-3">
              <label>Status</label>
              <select
                className="form-select"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="open">Otvoren</option>
                <option value="closed">Zatvoren</option>
                <option value="in_progress">U tijeku</option>
              </select>
            </div>
          )}
        </div>

        <div className="col-md-6">
          <h4>Lokacija kviza</h4>

          <div className="mb-3">
            <label>Odaberi lokaciju (klikni na mapu)*</label>
            <GoogleMap 
              apiKey={apiKey}
              center={selectedLocation || { lat: 45.8150, lng: 15.9819 }}
              zoom={selectedLocation ? 15 : 12}
              onLocationSelect={handleLocationSelect}
              marker={selectedLocation ? {
                lat: selectedLocation.lat,
                lng: selectedLocation.lng,
                title: "Odabrana lokacija"
              } : undefined}
            />
            {selectedLocation && (
              <p className="mt-2 small text-success">
                ✓ Odabrana lokacija: {selectedLocation.lat.toFixed(6)}, {selectedLocation.lng.toFixed(6)}
              </p>
            )}
            {isFetchingAddress ? (
              <p className="mt-2 small">Dohvaćanje adrese...</p>
            ) : (
              address && <p className="mt-2 small"><strong>Adresa:</strong> {address}</p>
            )}
          </div>

          <div className="mb-3">
            <label>Naziv lokacije*</label>
            <input
              type="text"
              className="form-control"
              value={locationName}
              onChange={(e) => setLocationName(e.target.value)}
              placeholder="npr. Gradski muzej"
            />
          </div>

          <div className="mb-3">
            <label>Grad*</label>
            <input
              type="text"
              className="form-control"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              placeholder="npr. Zagreb"
            />
          </div>

          <div className="mb-3">
            <label>Kapacitet*</label>
            <input
              type="number"
              className="form-control"
              value={capacity}
              onChange={(e) => setCapacity(Number(e.target.value))}
              min="1"
              placeholder="Broj mjesta"
            />
          </div>
        </div>
      </div>

      <div className="mt-4">
        <button
          className="btn btn-primary me-2"
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading 
            ? (isEditMode ? "Ažuriram..." : "Kreiram...") 
            : (isEditMode ? "Ažuriraj kviz" : "Kreiraj kviz i lokaciju")
          }
        </button>
        <button
          className="btn btn-secondary"
          onClick={() => window.history.back()}
          disabled={loading}
        >
          Otkaži
        </button>
      </div>
    </div>
  );
}
