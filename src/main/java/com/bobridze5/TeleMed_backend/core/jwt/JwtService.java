package com.bobridze5.TeleMed_backend.core.jwt;

import com.bobridze5.TeleMed_backend.core.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${application.secret}")
    private String secret;

    @Getter
    @Value("${application.access.lifetimeMs}")
    private long accessLifeTimeMs;

    @Getter
    @Value("${application.refresh.lifetimeMs}")
    private long refreshLifeTimeMs;

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessLifeTimeMs))
                .signWith(getKey())
                .claim("type", TOKEN_TYPE_ACCESS)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshLifeTimeMs))
                .signWith(getKey())
                .claim("type", TOKEN_TYPE_REFRESH)
                .compact();
    }

    public Claims parse(String token) throws IllegalArgumentException, JwtException {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        Claims claims = parse(token);
        return claims.getSubject();
    }

    public boolean isAccessTokenValid(String accessToken) {
        return isTokenValid(accessToken, TOKEN_TYPE_ACCESS);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return isTokenValid(refreshToken, TOKEN_TYPE_REFRESH);
    }

    private boolean isTokenValid(String token, String type) {
        try {
            Claims claims = parse(token);
            return type.equals(claims.get("type"));
        } catch (JwtException | IllegalStateException e) {
            return false;
        }
    }
}
