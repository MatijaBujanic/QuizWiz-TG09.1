import { useEffect, useMemo, useState } from "react";
import api from "../api/api";
import { useAuth } from "../context/AuthContext";

type Props = {
    quizId: number;
    onChanged?: () => void;
};

const StarRatingControl = ({ quizId, onChanged }: Props) => {
    const { token, isAuthenticated } = useAuth();

    const [myRating, setMyRating] = useState<number>(0);
    const [hover, setHover] = useState<number>(0);
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const stars = useMemo(() => [1, 2, 3, 4, 5], []);

    const authHeaders = useMemo(() => {
        return token ? { Authorization: `Bearer ${token}` } : {};
    }, [token]);

    useEffect(() => {
        const loadMyRating = async () => {
            setLoading(true);
            setError(null);

            try {
                const res = await api.get(`/api/quizzes/${quizId}/rating`, {
                    headers: authHeaders,
                });

                const data = res.data;

                let value = 0;
                if (typeof data === "number") value = data;
                else if (data && typeof data.rating === "number") value = data.rating;
                else if (data && typeof data.myRating === "number")
                    value = data.myRating;
                else if (data && typeof data.userRating === "number")
                    value = data.userRating;

                value = Math.max(0, Math.min(5, Math.floor(value)));
                setMyRating(value);
            } catch (e: any) {
                console.warn(
                    "loadMyRating failed:",
                    e?.response?.status,
                    e?.response?.data,
                );
                setMyRating(0);
            } finally {
                setLoading(false);
            }
        };

        // ako nisi prijavljen, nema smisla pozivati
        if (!isAuthenticated || !token) {
            setMyRating(0);
            setLoading(false);
            return;
        }

        loadMyRating();
    }, [quizId, authHeaders, isAuthenticated, token]);

    const rate = async (value: number) => {
        setBusy(true);
        setError(null);

        try {
            if (!token) {
                setError("Trebaš biti prijavljen da ocijeniš.");
                return;
            }

            await api.post(
                `/api/quizzes/${quizId}/rate`,
                { rating: value },
                { headers: authHeaders },
            );

            setMyRating(value);
            onChanged?.();
        } catch (e: any) {
            console.error("rate failed:", e?.response?.status, e?.response?.data);
            setError(
                typeof e?.response?.data === "string"
                    ? e.response.data
                    : e?.response?.data?.message || "Ne mogu spremiti ocjenu.",
            );
        } finally {
            setBusy(false);
        }
    };

    const removeRating = async () => {
        setBusy(true);
        setError(null);

        try {
            if (!token) {
                setError("Trebaš biti prijavljen da obrišeš ocjenu.");
                return;
            }

            await api.delete(`/api/quizzes/${quizId}/rate`, {
                headers: authHeaders,
            });

            setMyRating(0);
            onChanged?.();
        } catch (e: any) {
            console.error(
                "removeRating failed:",
                e?.response?.status,
                e?.response?.data,
            );
            setError(
                typeof e?.response?.data === "string"
                    ? e.response.data
                    : e?.response?.data?.message || "Ne mogu obrisati ocjenu.",
            );
        } finally {
            setBusy(false);
        }
    };

    const displayValue = hover || myRating;

    return (
        <div className="d-flex align-items-center gap-2">
            <div
                className="d-flex align-items-center"
                style={{ opacity: busy ? 0.6 : 1 }}
            >
                {stars.map((s) => (
                    <button
                        key={s}
                        type="button"
                        className="btn p-0 border-0"
                        disabled={loading || busy || !token}
                        onMouseEnter={() => setHover(s)}
                        onMouseLeave={() => setHover(0)}
                        onClick={() => rate(s)}
                        aria-label={`Ocijeni ${s} od 5`}
                        style={{
                            background: "transparent",
                            cursor: loading || busy ? "not-allowed" : "pointer",
                            fontSize: "1.25rem",
                            lineHeight: 1,
                        }}
                    >
                        {s <= displayValue ? "★" : "☆"}
                    </button>
                ))}
            </div>

            <button
                type="button"
                className="btn btn-sm btn-outline-danger"
                disabled={loading || busy || myRating === 0 || !token}
                onClick={removeRating}
                title="Obriši moju ocjenu"
            >
                🗑
            </button>

            <div className="small text-muted ms-1">
                {loading
                    ? "Učitavam..."
                    : myRating
                        ? `Moja ocjena: ${myRating}/5`
                        : "Nisi ocijenio"}
            </div>

            {error && <div className="small text-danger ms-2">{error}</div>}
        </div>
    );
};

export default StarRatingControl;
