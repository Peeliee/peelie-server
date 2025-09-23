package com.peelie.auth.application;

import com.peelie.common.jwt.JwtUtil;
import com.peelie.auth.domain.OauthInfo;
import com.peelie.auth.domain.OauthProvider;
import com.peelie.auth.domain.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthFacade {

    private final OauthService oauthService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(OauthProvider provider, String code) {

        OauthInfo oauthInfo = oauthService.authenticate(provider, code);
        boolean isLinked = oauthInfo.isLinked();

        if (isLinked) {
            // 기존 유저: 정상 엑세스 토큰 발급
            String completedJwt = jwtUtil.createCompletedJwt(oauthInfo.getUserId());
            return new LoginResponse(completedJwt, "completed", false);
        } else {
            //신규 유저: 임시 토큰 발급
            String pendingJwt = jwtUtil.createPendingJwt(oauthInfo.getId());
            return new LoginResponse(pendingJwt, "pending", true);
        }
    }
}
