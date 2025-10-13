package com.peelie.common.interceptor;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.exception.AuthException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            // 컨트롤러 메서드가 아니면 인터셉터의 검증 로직을 일단 적용 안함. (스웨거 경로)
            return true;
        }

        String authorizationHeader = request.getHeader("Authorization");

        // 1. 헤더 존재 여부 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 2. 토큰 추출 및 검증
        String token = authorizationHeader.substring(7);
        jwtUtil.validateToken(token);

        // 유저 정보를 ThreadLocal로 저장
        Claims claims = jwtUtil.getClaims(token);

        Long userId = Long.parseLong(claims.getSubject());

        UserContextHolder.setUserId(userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContextHolder.clear();
    }
}
