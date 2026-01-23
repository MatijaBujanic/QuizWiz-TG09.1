import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { getEmailFromToken } from "../utils/authMapper"; // prilagodi putanju ako treba

type Me = {
    user_id: number;
    email: string;
    username: string;
    role: string;
};

type RoleLookupResponse = {
    email: string;
    role: string;
    username: string;
    userId: number;
};

type StarRatingControlProps = {
    quizId: number;
    onChanged?: () => void;
};

const StarRatingControl = ({ quizId, onChanged }: StarRatingControlProps) => {
    const { token, isAuthenticated } = useAuth();

    const [me, setMe] = useState<Me | null>(null);
    const [myRating, setMyRating] = useState<number>(0);
    const [hover, setHover] = useState<number>(0);
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const stars = useMemo(() => [1, 2, 3, 4, 5], []);

    // axios instance za poziv user role (s Authorization headerom)
    const axiosInstance = useMemo(
        () =>
            axios.create({
                baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
                withCredentials: true,
                headers: token ? { Authorization: `Bearer ${token}` } : undefined,
            }),
        [token],
    );

    // 1) Load user info (me) from /api/users/role using email extracted from token
    useEffect(() => {
        const loadMe = async () => {
            if (!isAuthenticated || !token) {
                setMe(null);
                return;
            }

            const email = getEmailFromToken(token);
            if (!email) {
                console.warn("Could not extract email from JWT token.");
                setMe(null);
                return;
            }

            try {
                const res = await axiosInstance.get<RoleLookupResponse>("/api/users/role", {
                    params: { email },
                });

                const dto = res.data;
                const mapped: Me = {
                    user_id: dto.userId,
                    email: dto.email,
                    username: dto.username,
                    role: dto.role,
                };
                setMe(mapped);
            } catch (err) {
                console.error("Failed to load user via /api/users/role:", err);
                setMe(null);
            }
        };

        loadMe();
    }, [axiosInstance, isAuthenticated, token]);

    // 2) Load my rating
    useEffect(() => {
        const loadMyRating = async () => {
            if (!me?.user_id) {
                setMyRating(0);
                setLoading(false);
                return;
            }

            setLoading(true);
            setError(null);

            try {
                const res = await axios.get(`/api/quizzes/${quizId}/rating`, {
                    baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
                    params: { userId: me.user_id },
                });

                const data = res.data;
                let value = 0;
                if (typeof data === "number") value = data;
                else if (data && typeof data.rating === "number") value = data.rating;
                else if (data && typeof data.myRating === "number") value = data.myRating;
                else if (data && typeof data.userRating === "number") value = data.userRating;

                value = Math.max(0, Math.min(5, Math.floor(value)));
                setMyRating(value);
            } catch (e: any) {
                console.warn("loadMyRating failed:", e?.response?.status, e?.response?.data);
                setMyRating(0);
            } finally {
                setLoading(false);
            }
        };

        loadMyRating();
    }, [quizId, me?.user_id]);

    // Rate quiz (POST)
    const rate = async (value: number) => {
        if (!me?.user_id) {
            setError("Moraš biti prijavljen da ocijeniš.");
            return;
        }

        setBusy(true);
        setError(null);

        try {
            await axios.post(
                `/api/quizzes/${quizId}/rate`,
                { rating: value },
                {
                    baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
                    params: { userId: me.user_id },
                },
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

    // Remove rating (DELETE)
    const removeRating = async () => {
        if (!me?.user_id) {
            setError("Moraš biti prijavljen da obrišeš ocjenu.");
            return;
        }

        setBusy(true);
        setError(null);

        try {
            await axios.delete(`/api/quizzes/${quizId}/rate`, {
                baseURL: "https://quizwiz-tg091-production-504c.up.railway.app",
                params: { userId: me.user_id },
            });

            setMyRating(0);
            onChanged?.();
        } catch (e: any) {
            console.error("removeRating failed:", e?.response?.status, e?.response?.data);
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
                        disabled={loading || busy || !me?.user_id}
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
                disabled={loading || busy || myRating === 0 || !me?.user_id}
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
