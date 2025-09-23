package com.peelie.auth.domain;

/**
 * Oauth 2.0 에서의 Client 역할 인터페이스 - Resouce Owner(사용자)를 대신하여 Resource Server(서비스 제공자)로부터 자원 요청
 * 각 Oauth 제공자별로 구현필요
 */
public interface OauthClient {
    OauthProvider getProvider();

    /**
     * Authorization Code 를 이용하여 Oauth 제공자로부터 사용자 정보 요청
     * code를 accessToken으로 바꾸고, 그 accessToken으로 최종 사용자 정보를 얻어와서 OauthInfo 객체로 만들어주는 두 단계 작업
     * @param code
     * @return
     */
    OauthResourceResponse fetchProfile(String code);
}