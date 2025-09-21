package com.peelie.auth.domain;

import lombok.Builder;
import lombok.Getter;

//OauthClient가 외부 소셜 API 서버로부터 받아온 사용자 프로필 정보를 담는 객
@Getter
public class OauthResourceResponse {

    private final OauthProvider provider;
    private final String oid;
    private final String email;

    @Builder
    public OauthResourceResponse(OauthProvider provider, String oid, String email) {
        this.provider = provider;
        this.oid = oid;
        this.email = email;
    }
}