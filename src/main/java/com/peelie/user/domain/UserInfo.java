package com.peelie.user.domain;

import lombok.Getter;

@Getter
public class UserInfo {
    private final Long id;
    private final String userToken;
    private final String userName;
    private final String imageUrl;
    private final String instagramId;

    public UserInfo(User user) {
        this.id = user.getId();
        this.userToken = user.getUserToken();
        this.userName = user.getUserName();
        this.imageUrl = user.getImageUrl();
        this.instagramId = user.getInstagramId();
    }
}
