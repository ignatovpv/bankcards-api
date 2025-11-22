package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.RequiredArgsConstructor;
import com.example.bankcards.config.JwtProperties;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties properties;

    private SecretKey getKey() {
        byte[] decoded = Base64.getDecoder().decode(properties.secret());
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateToken(String username, String role) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime exp = now.plus(properties.expiration());

        SecretKey key = getKey();
        MacAlgorithm alg = Jwts.SIG.HS256;

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(exp.toInstant()))
                .signWith(key, alg)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser().verifyWith(getKey()).build()
                .parseSignedClaims(token)
                .getPayload();
        Object roleClaim = claims.get("role");
        return roleClaim != null ? roleClaim.toString() : null;
    }

    public void validateToken(String token) {
        Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token); // will throw JwtException if the token is invalid or expired
    }
}
