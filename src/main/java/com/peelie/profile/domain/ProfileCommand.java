package com.peelie.profile.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileCommand {

    private final Long userId;
    private final String userName;
    private final String instagramId;
    private final String imageUrl;

    public Profile toEntity() {
        return Profile.builder()
                .userId(userId)
                .userName(userName)
                .instagramId(instagramId)
                .profileImageUrl(imageUrl)
                .build();

    }
}
