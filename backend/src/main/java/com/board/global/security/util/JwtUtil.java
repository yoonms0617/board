package com.board.global.security.util;

import com.board.global.security.exception.ExpiredTokenException;
import com.board.global.security.exception.InvalidTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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

    public Claims getPayload(String token) {
        return getClaims(token).getBody();
    }

    public void validateToken(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            checkExpireToken(claims.getBody());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    private Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            throw new InvalidTokenException();
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        }
    }

    private void checkExpireToken(Claims body) {
        if (body.getExpiration().before(new Date())) {
            throw new ExpiredTokenException();
        }
    }

}
