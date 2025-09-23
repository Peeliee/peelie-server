package com.peelie.auth.domain;

public interface OauthService {
    // 소셜 정보로 OauthAccount를 찾거나 생성
    OauthInfo authenticate(OauthProvider provider, String code);
    // 전달된 OauthAccount에 User 정보를 연결
    OauthInfo linkUser(String oauthAccountToken, Long userId);
    // User ID로 User 대체키 조회 (jwt 토큰 생성용)
    String getUserToken(Long userId);
}
