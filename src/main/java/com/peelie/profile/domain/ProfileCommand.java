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
        // 카드 정보 필드 추가
        private final String stage1CardTitle;
        private final String stage1CardSubtitle;
        private final String stage1CardContent;
        private final String stage2CardTitle;
        private final String stage2CardSubtitle;
        private final String stage2CardContent;
        private final String stage3CardTitle;
        private final String stage3CardSubtitle;
        private final String stage3CardContent;
    }
}
