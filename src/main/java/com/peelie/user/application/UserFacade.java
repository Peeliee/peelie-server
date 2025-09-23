package com.peelie.user.application;

import com.peelie.common.jwt.JwtUtil;
import com.peelie.auth.domain.OauthService;
import com.peelie.user.domain.UserInfo;
import com.peelie.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final OauthService oauthService;
    private final JwtUtil jwtUtil;

    @Transactional
    public String registerUser(Long oauthAccountId) {
        // 1. 새로운 User 생성
        UserInfo newUserInfo = userService.registerUser();
        // 2. 생성된 User의 ID를 OauthAccount에 연결
        oauthService.linkUser(oauthAccountId, newUserInfo.getUserId());
        // 3. User의 UserId로 최종 엑세스 토큰(Completed Token) 생성
        return jwtUtil.createCompletedJwt(newUserInfo.getUserId());
    }

    public UserInfo retrieveUserInfo(Long userId) {
        return userService.getUserInfo(userId);
    }
}
