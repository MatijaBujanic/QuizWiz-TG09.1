package com.example.demo.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
@Service
public class JwtTokenService {

    private final String SECRET; // Use env var in prod
    private static final long EXPIRATION_MS = 3600_000; // 1 hour

    public JwtTokenService() {
        this.SECRET = Base64.getEncoder()
                .encodeToString(
                        Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()
                );
    }

    // ⚠️ OVO OSTAVLJAMO KAKO JE
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // ✅ DODANO – koristi subject kao email
    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ DODANO – validacija tokena
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

