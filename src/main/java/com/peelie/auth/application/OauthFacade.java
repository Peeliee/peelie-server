package com.peelie.auth.application;

import com.peelie.auth.domain.OauthInfo;
import com.peelie.auth.domain.OauthProvider;
import com.peelie.auth.domain.OauthService;
import com.peelie.common.jwt.JwtUtil;
import com.peelie.user.domain.UserInfo;
import com.peelie.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthFacade {

    private final OauthService oauthService;
    private final UserService userService; // ★ User 도메인 서비스 주입
    private final JwtUtil jwtUtil;

    public String login(OauthProvider provider, String code) {

        OauthInfo oauthInfo = oauthService.authenticate(provider, code);

        Long userId;

        if (oauthInfo.getUserId() != null) {
            // 기존 유저
            userId = oauthInfo.getUserId();
        } else {
            //신규 유저
            UserInfo newUserInfo = userService.registerUser();
            oauthService.linkUser(oauthInfo.getId(), newUserInfo.getUserId());
            userId = newUserInfo.getUserId();
        }

        // 5. (Common) 최종 COMPLETED 토큰 발급
        String accessToken = jwtUtil.createJwt(userId.toString());

        return accessToken;
    }
}
