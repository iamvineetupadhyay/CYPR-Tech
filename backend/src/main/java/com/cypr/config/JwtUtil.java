package com.cypr.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${cypr.jwt.secret}")
    private String secret;

    @Value("${cypr.jwt.expiration-hours}")
    private int expirationHours;

    public String generateToken(Long userId, String email) {
        return createToken(userId, email, expirationHours * 3600 * 1000L);
    }

    public String generateRefreshToken(Long userId, String email) {
        return createToken(userId, email, expirationHours * 3600 * 1000L * 24); // 24x longer for refresh
    }

    private String createToken(Long userId, String email, long expirationMillis) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(algorithm);
    }

    public String extractEmail(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userId").asLong();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        return extractEmail(token) != null;
    }
}
