export interface JwtPayload {
  sub?: string;
  email?: string;
  role?: string;
  exp?: number;
  iat?: number;
}

export function decodeJwt(token: string): JwtPayload | null {
  try {
    const payload = token.split(".")[1];
    const decodedJson = atob(payload);
    return JSON.parse(decodedJson);
  } catch (e) {
    console.error("Failed to decode JWT", e);
    return null;
  }
}

export function getEmailFromToken(token: string): string | null {
  const decoded = decodeJwt(token);
  return decoded?.sub || decoded?.email || null;
}

export function getRoleFromToken(token: string): string | null {
  const decoded = decodeJwt(token);
  return decoded?.role || null;
}