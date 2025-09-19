package com.peelie.oauth.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * OauthClient가 외부 소셜 API 서버로부터 받아온 사용자 프로필 정보를 담는 객체
 */
@Getter
public class OauthResourceResponse {

    private final OauthProvider provider;
    private final String oid; // 소셜 서비스에서의 사용자 고유 ID
    private final String email;

    @Builder
    public OauthResourceResponse(OauthProvider provider, String oid, String email) {
        this.provider = provider;
        this.oid = oid;
        this.email = email;
    }
}