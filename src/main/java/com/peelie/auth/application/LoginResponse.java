package com.peelie.auth.application;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String jwt;      // jwt 토큰 (신규 유저일 경우 임시 토큰, 기존 유저일 경우 최종 토큰)
    private final String jwtType;  // jwt 토큰 타입 ("pending" or "completed")
    private final boolean isNewUser; // 신규 유저 여부

    public LoginResponse(String jwt, String jwtType, boolean isNewUser) {
        this.jwt = jwt;
        this.jwtType = jwtType;
        this.isNewUser = isNewUser;
    }
}
