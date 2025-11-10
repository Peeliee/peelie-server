package com.peelie.common.jwt;

import com.peelie.common.exception.AuthException;
import com.peelie.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public String createJwt(String subject) {
        Date now = new Date();
        long validityInMilliseconds = accessTokenValidityInMinutes * 60 * 1000;
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(subject)   // subject 설정(토큰 발행자 정보)
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

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            // 서명 오류 또는 형식 오류
            throw new AuthException(ErrorCode.TOKEN_INVALID);
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            // 토큰이 null 또는 빈 문자열
            throw new AuthException(ErrorCode.TOKEN_INVALID);
        }
    }
}