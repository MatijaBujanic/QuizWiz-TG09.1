import { useState, useEffect } from "react";
import GoogleMap from "../components/GoogleMap";

export default function CreateQuizPage() {
  const [selectedLocation, setSelectedLocation] = useState<{
    lat: number;
    lng: number;
  } | null>(null);
  const [address, setAddress] = useState<string | null>(null);
  const [locationName, setLocationName] = useState<string>("");
  const [city, setCity] = useState<string>("");
  const [capacity, setCapacity] = useState<number>(0);
  const [isFetchingAddress, setIsFetchingAddress] = useState<boolean>(false);

  const apiKey = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

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
        `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=${apiKey}`
      );
      const data = await response.json();
      if (data.results && data.results.length > 0) {
        setAddress(data.results[0].formatted_address);
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

  const handleSubmit = () => {
    if (!selectedLocation || !locationName || !city || capacity <= 0) {
      alert("Molimo popunite sve podatke ispravno.");
      return;
    }
    console.log({
      location: selectedLocation,
      address,
      locationName,
      city,
      capacity,
    });
    alert("Lokacija je uspješno spremljena!");
  };

  useEffect(() => {
    const existingQuizLocation = { lat: 45.8150, lng: 15.9819 }; // Example: Zagreb
    setSelectedLocation(existingQuizLocation);
    fetchAddress(existingQuizLocation.lat, existingQuizLocation.lng);
  }, []);

  return (
    <div className="container mt-4">
      <h2>Kreiraj novi kviz</h2>
      
      <div className="mb-3">
        <label>Naziv kviza</label>
        <input type="text" className="form-control" />
      </div>

      <div className="mb-3">
        <label>Odaberi lokaciju (klikni na mapu)</label>
        <GoogleMap 
          apiKey={apiKey}
          center={selectedLocation || { lat: 45.8150, lng: 15.9819 }} // Default to Zagreb
          zoom={12}
          onLocationSelect={handleLocationSelect}
        />
        {selectedLocation && (
          <p className="mt-2">
            Odabrana lokacija: {selectedLocation.lat.toFixed(6)}, {selectedLocation.lng.toFixed(6)}
          </p>
        )}
        {isFetchingAddress ? (
          <p className="mt-2">Dohvaćanje adrese...</p>
        ) : (
          address && <p className="mt-2">Adresa: {address}</p>
        )}
      </div>

      <div className="mb-3">
        <label>Naziv lokacije</label>
        <input
          type="text"
          className="form-control"
          value={locationName}
          onChange={(e) => setLocationName(e.target.value)}
        />
      </div>

      <div className="mb-3">
        <label>Grad</label>
        <input
          type="text"
          className="form-control"
          value={city}
          onChange={(e) => setCity(e.target.value)}
        />
      </div>

      <div className="mb-3">
        <label>Kapacitet</label>
        <input
          type="number"
          className="form-control"
          value={capacity}
          onChange={(e) => setCapacity(Number(e.target.value))}
        />
      </div>

      <button className="btn btn-primary" onClick={handleSubmit}>
        Spremi lokaciju
      </button>
    </div>
  );
}