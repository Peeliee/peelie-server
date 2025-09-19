package com.peelie.oauth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OauthProvider {

    KAKAO("Kakao"),;

    private final String description;
}
