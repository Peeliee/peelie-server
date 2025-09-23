package com.peelie.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.access-token-minutes}")
    private long accessTokenValidityInMinutes;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    // 1: COMPLETED 상태의 최종 액세스 토큰 생성
    public String createCompletedJwt(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("status", "COMPLETED");
        return createJwt(userId.toString(), claims);
    }

    // 2: PENDING 상태의 임시 액세스 토큰 생성
    public String createPendingJwt(Long oauthAccountId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("status", "PENDING");
        return createJwt(oauthAccountId.toString(), claims);
    }

    private String createJwt(String subject, Map<String, Object> claims) {
        Date now = new Date();
        long validityInMilliseconds = accessTokenValidityInMinutes * 60 * 1000;
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(subject)   // subject 설정
                .claims(claims)     // 클레임을 builder에 직접 설정
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}