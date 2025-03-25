package com.study.mybatisplus.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private static final String KEY = "lof3ad80";

    // Generate token from claims
    public static String genToken(Map<String, Object> claims) {
        System.out.println("Generating token with claims: " + claims);
        String token = JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 12))
                .sign(Algorithm.HMAC256(KEY));
        System.out.println("Generated token: " + token);
        return token;
    }

    // Parse token to get claims
    public static Map<String, Object> parseToken(String token) {
        try {
            System.out.println("Parsing token: " + token);
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Object> claims = jwt.getClaim("claims").asMap();
            System.out.println("Token parsed, claims: " + claims);
            return claims;
        } catch (JWTVerificationException e) {
            System.out.println("Token verification failed: " + e.getMessage());
            throw e;
        }
    }

    // Check if token is expired
    public static boolean isTokenExpired(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Get expiration date from token
    public static Date getExpirationFromToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(KEY)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }
}