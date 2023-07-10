package com.board.global.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenInMilliseconds;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey,
                   @Value("${jwt.access-token.expire-length}") long accessTokenInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenInMilliseconds = accessTokenInMilliseconds;
    }

    public String createAccessToken(String username, String role) {
        return createToken(username, role, accessTokenInMilliseconds);
    }

    private String createToken(String username, String role, long tokenInMilliseconds) {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + tokenInMilliseconds);
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
