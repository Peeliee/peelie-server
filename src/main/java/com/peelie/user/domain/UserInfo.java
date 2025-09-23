package com.peelie.user.domain;

import lombok.Getter;

@Getter
public class UserInfo {
    private final Long userId;

    public UserInfo(User user) {
        this.userId = user.getId();
    }
}
