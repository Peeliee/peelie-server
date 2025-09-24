package com.peelie.user.interfaces;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jwt.JwtUtil;
import com.peelie.common.response.SuccessResponse;
import com.peelie.user.application.UserFacade;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserFacade userFacade;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public SuccessResponse register(HttpServletRequest request) {

        // TODO 토큰 검증 처리 컨트롤러에서 분리 필요

        // 1. Authorization 헤더에서 JWT 토큰 추출
        String bearerToken = request.getHeader("Authorization");

        // 2. 토큰 유효성 검사 (존재 여부 및 "Bearer " 접두사 확인)
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new BaseException("유효하지 않은 토큰입니다.", ErrorCode.VALIDATION_ERROR);
        }
        String jwt = bearerToken.substring(7);

        // 3. 토큰의 Claims 파싱 및 PENDING 상태 검증
        Claims claims = jwtUtil.getClaims(jwt);
        String tokenStatus = claims.get("status", String.class);

        if (!"PENDING".equals(tokenStatus)) {
            throw new BaseException("회원가입용 임시 토큰이 아닙니다.", ErrorCode.VALIDATION_ERROR);
        }

        // 4. Claims에서 subject(oauthAccountId) 추출
        Long oauthAccountId = Long.valueOf(claims.getSubject());

        // 5. UserFacade를 통해 회원가입 및 계정 연결 처리
        String completedAccessToken = userFacade.registerUser(oauthAccountId);

        // 6. 최종 토큰(Completed Access Token) 반환
        return SuccessResponse.ok(completedAccessToken);
    }
}