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

        //TODO: 재현님 카드 기능 완성 후 실제 데이터 반영
        this.card = new Card(
                new Card.StageInfo("임시 Stage1 Title", "Stage1 Subtitle", "Stage1 Content"),
                new Card.StageInfo("임시 Stage2 Title", "Stage2 Subtitle", "Stage1 Content"),
                new Card.StageInfo("임시 Stage3 Title", "Stage3 Subtitle", "Stage1 Content")
        );
    }

    @Getter
    @AllArgsConstructor
    public static class BioInfo {
        // TODO: 임시 메시지 나중에 실제 자기소개 메시지 나오면 변경
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