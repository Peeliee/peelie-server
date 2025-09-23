package com.peelie.user.domain;

import lombok.Getter;

@Getter
public class UserInfo {
    private final Long userId;
    private final String userToken;

    public UserInfo(User user) {
        this.userId = user.getId();
        this.userToken = user.getUserToken();
    }
}
