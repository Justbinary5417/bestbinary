package com.justbinary.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
        "justbinary_secret_key_must_be_32chars_long!!";

    private static final long EXPIRATION =
        86400000L;

    private Key getKey() {
        return Keys.hmacShaKeyFor(
            SECRET.getBytes());
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(
                System.currentTimeMillis()
                + EXPIRATION))
            .signWith(getKey(),
                SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration()
            .before(new Date());
    }

    public boolean isTokenValid(
            String token, String email) {
        try {
            String extracted = extractEmail(token);
            return extracted.equals(email)
                && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}