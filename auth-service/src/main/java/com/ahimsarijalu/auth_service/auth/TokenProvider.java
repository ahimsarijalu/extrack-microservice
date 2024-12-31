package com.ahimsarijalu.auth_service.auth;

import com.ahimsarijalu.auth_service.user.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String JWT_SECRET;

    public String generateAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                    .withSubject(user.getEmail())
                    .withClaim("email", user.getEmail())
                    .withExpiresAt(genAccessExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new JWTCreationException("Error while generating token: " + e.getMessage(), e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Token has expired", e.getExpiredOn());
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Error while validating token", e);
        }
    }


    private Instant genAccessExpirationDate() {
        return LocalDateTime.now()
                .plusHours(24)
                .toInstant(ZoneOffset.of("+07:00"));
    }
}
