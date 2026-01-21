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
}

function MyMapComponent({ center, zoom, onClick }: MapProps) {
  const ref = useRef<HTMLDivElement>(null);
  const mapRef = useRef<google.maps.Map | null>(null);

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

  return <div ref={ref} style={{ height: "400px", width: "100%" }} />;
}

interface GoogleMapProps {
  apiKey: string;
  center?: { lat: number; lng: number };
  zoom?: number;
  onLocationSelect?: (location: { lat: number; lng: number }) => void;
}

export default function GoogleMap({ 
  apiKey, 
  center = { lat: 45.8150, lng: 15.9819 }, // Zagreb
  zoom = 12,
  onLocationSelect 
}: GoogleMapProps) {
  return (
    <Wrapper apiKey={apiKey}>
      <MyMapComponent 
        center={center} 
        zoom={zoom} 
        onClick={onLocationSelect}
      />
    </Wrapper>
  );
}