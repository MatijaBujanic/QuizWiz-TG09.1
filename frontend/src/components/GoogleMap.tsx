import { Wrapper } from "@googlemaps/react-wrapper";
import { useRef, useEffect } from "react";

// Declare global google object
declare global {
  interface Window {
    google: any;
  }
}

interface MapProps {
  center: { lat: number; lng: number };
  zoom: number;
  onClick?: (location: { lat: number; lng: number }) => void;
  marker?: { lat: number; lng: number; title?: string };
}

function MyMapComponent({ center, zoom, onClick, marker }: MapProps) {
  const ref = useRef<HTMLDivElement>(null);
  const mapRef = useRef<google.maps.Map | null>(null);
  const markerRef = useRef<google.maps.Marker | null>(null);

  useEffect(() => {
    if (ref.current && !mapRef.current && window.google) {
      mapRef.current = new window.google.maps.Map(ref.current, {
        center,
        zoom,
        mapTypeControl: true,
        streetViewControl: false,
        fullscreenControl: true,
      });

      if (onClick && mapRef.current) {
        mapRef.current.addListener("click", (event: any) => {
          if (event.latLng) {
            const location = {
              lat: event.latLng.lat(),
              lng: event.latLng.lng(),
            };
            onClick(location);
          }
        });
      }
    }
  }, [center, zoom, onClick]);

  // Marker effect
  useEffect(() => {
    if (mapRef.current && marker && window.google) {
      // Ukloni stari marker
      if (markerRef.current) {
        markerRef.current.setMap(null);
      }

      // Dodaj novi marker
      markerRef.current = new window.google.maps.Marker({
        position: { lat: marker.lat, lng: marker.lng },
        map: mapRef.current,
        title: marker.title || "Lokacija kviza",
        animation: window.google.maps.Animation.DROP,
      });

      // Centriraj mapu na marker
      mapRef.current.setCenter({ lat: marker.lat, lng: marker.lng });
    }

    // Cleanup
    return () => {
      if (markerRef.current) {
        markerRef.current.setMap(null);
      }
    };
  }, [marker]);

  return <div ref={ref} style={{ height: "400px", width: "100%" }} />;
}

interface GoogleMapProps {
  apiKey: string;
  center?: { lat: number; lng: number };
  zoom?: number;
  onLocationSelect?: (location: { lat: number; lng: number }) => void;
  marker?: { lat: number; lng: number; title?: string };
}

export default function GoogleMap({ 
  apiKey, 
  center = { lat: 45.8150, lng: 15.9819 }, // Zagreb
  zoom = 12,
  onLocationSelect,
  marker
}: GoogleMapProps) {
  return (
    <Wrapper apiKey={apiKey}>
      <MyMapComponent 
        center={center} 
        zoom={zoom} 
        onClick={onLocationSelect}
        marker={marker}
      />
    </Wrapper>
  );
}