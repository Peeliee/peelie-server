package com.peelie.profile.domain;

import lombok.Builder;
import lombok.Getter;

public class ProfileCommand {

    @Getter
    @Builder
    public static class RegisterCommand {
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

    @Getter
    @Builder
    public static class UpdateCommand {
        private final String userName;
        private final String instagramId;
        private final String imageUrl;
        private final InteractionStyle interactionStyle;
        private final String stage1Bio;
        private final String stage2Bio;
        private final String stage3Bio;

    }
}
