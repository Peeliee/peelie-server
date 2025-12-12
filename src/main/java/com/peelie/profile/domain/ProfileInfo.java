package com.peelie.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileInfo {
    private final Long userId;
    private final String userName;
    private final String profileImageUrl;
    private final String instagramId;
    private final List<BioInfo> bio;
    private final InteractionStyle interactionStyle;
    private final Card card;

    public ProfileInfo(Profile profile) {
        this.userId = profile.getUserId();
        this.userName = profile.getUserName();
        this.profileImageUrl = profile.getProfileImageUrl();
        this.instagramId = profile.getInstagramId();

        this.bio = List.of(
                new BioInfo("stage0", BioInfo.STAGE0_BIO),
                new BioInfo("stage1", profile.getStage1Bio()),
                new BioInfo("stage2", profile.getStage2Bio()),
                new BioInfo("stage3", profile.getStage3Bio())
        );

        this.interactionStyle = profile.getInteractionStyle();

        this.card = new Card(
                new Card.StageInfo(profile.getStage1Card().getTitle(),
                        profile.getStage1Card().getSubtitle(),
                        profile.getStage1Card().getContent()),
                new Card.StageInfo(profile.getStage2Card().getTitle(),
                        profile.getStage2Card().getSubtitle(),
                        profile.getStage2Card().getContent()),
                new Card.StageInfo(profile.getStage3Card().getTitle(),
                        profile.getStage3Card().getSubtitle(),
                        profile.getStage3Card().getContent())
        );
    }

    @Getter
    @AllArgsConstructor
    public static class BioInfo {
        public static final String STAGE0_BIO = "자기소개가 없어요 퀴즈를 풀어주세요.";

        private final String stage;
        private final String bio;
    }

    @Getter
    @AllArgsConstructor
    public static class Card {
        private final StageInfo stage1;
        private final StageInfo stage2;
        private final StageInfo stage3;

        @Getter
        @AllArgsConstructor
        public static class StageInfo {
            private final String title;
            private final String subtitle;
            private final String content;
        }
    }
}