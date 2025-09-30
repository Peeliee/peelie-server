package com.peelie.profile.domain;

import lombok.Getter;

@Getter
public class ProfileInfo {
    private final Long userId;
    private final String userName;
    private final String profileImageUrl;
    private final String instagramId;
    private final String bio;
    private final InteractionStyle interactionStyle;

    public ProfileInfo(Profile profile) {
        this.userId = profile.getId();
        this.userName = profile.getUserName();
        this.profileImageUrl = profile.getProfileImageUrl();
        this.instagramId = profile.getInstagramId();
        this.bio = profile.getBio();
        this.interactionStyle = profile.getInteractionStyle();
    }
}