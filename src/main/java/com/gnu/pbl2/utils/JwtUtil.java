package com.gnu.pbl2.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${custom.security.key}")
    private String salt;

    @Value("${jwt.access-token.expiration-time}")
    private Long accessExpiration;

    @Value("${jwt.refresh-token.expiration-time}")
    private Long refreshExpiration;

    private Key getSigningKey() {
        return new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return createToken(claims, username, accessExpiration);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            System.out.println("JWT validation error: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
