import { useState } from "react";
import GoogleMap from "../components/GoogleMap";

export default function CreateQuizPage() {
  const [selectedLocation, setSelectedLocation] = useState<{
    lat: number;
    lng: number;
  } | null>(null);

  const handleLocationSelect = (location: { lat: number; lng: number }) => {
    setSelectedLocation(location);
    console.log("Selected location:", location);
  };

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
          apiKey=""
          onLocationSelect={handleLocationSelect}
        />
        {selectedLocation && (
          <p className="mt-2">
            Odabrana lokacija: {selectedLocation.lat.toFixed(6)}, {selectedLocation.lng.toFixed(6)}
          </p>
        )}
      </div>

      <button className="btn btn-primary">Spremi kviz</button>
    </div>
  );
}