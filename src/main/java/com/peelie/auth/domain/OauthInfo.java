package com.peelie.auth.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthInfo {

    private final Long id;
    private final String oauthAccountToken;
    private final OauthProvider provider;
    private final String oid;
    private final Long userId;
    private final String email;
    private final boolean linked;

    @Builder
    public OauthInfo(OauthAccount oauthAccount) {
        this.id = oauthAccount.getId();
        this.oauthAccountToken = oauthAccount.getOauthAccountToken();
        this.provider = oauthAccount.getProvider();
        this.oid = oauthAccount.getOid();
        this.userId = oauthAccount.getUserId();
        this.email = oauthAccount.getEmail();
        this.linked = oauthAccount.isLinked();
    }
}
