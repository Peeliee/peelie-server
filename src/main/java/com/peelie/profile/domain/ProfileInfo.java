package com.peelie.profile.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ProfileInfo {
    private final Long userId;
    private final String userName;
    private final String profileImageUrl;
    private final String instagramId;
    private final List<BioInfo> bio;
    private final InteractionStyle interactionStyle;
    private final Card card;

    public ProfileInfo(Profile profile,ObjectMapper objectMapper) {
        this.userId = profile.getUserId();
        this.userName = profile.getUserName();
        this.profileImageUrl = profile.getProfileImageUrl();
        this.instagramId = profile.getInstagramId();

        //TODO: 재현님 카드 기능 완성 후 실제 데이터 반영
        this.bio = List.of(
                new BioInfo("stage0", BioInfo.STAGE0_BIO),
                new BioInfo("stage1", profile.getStage1Bio()),
                new BioInfo("stage2", profile.getStage2Bio()),
                new BioInfo("stage3", profile.getStage3Bio())
                );

        this.interactionStyle = profile.getInteractionStyle();

        //TODO: 재현님 카드 기능 완성 후 실제 데이터 반영

        this.card = createCardFromProfile(profile,objectMapper);

    }
    private Card createCardFromProfile(Profile profile,ObjectMapper objectMapper) {
        if (profile.getCardInfoJson() != null && !profile.getCardInfoJson().isEmpty()) {
            try {
                // JSON 파싱하여 실제 데이터 사용
                Map<String, Object> cardData = objectMapper.readValue(profile.getCardInfoJson(), Map.class);

                Map<String, Object> stage1 = (Map<String, Object>) cardData.get("stage1");
                Map<String, Object> stage2 = (Map<String, Object>) cardData.get("stage2");
                Map<String, Object> stage3 = (Map<String, Object>) cardData.get("stage3");

                return new Card(
                        new Card.StageInfo(
                                (String) stage1.get("title"),
                                (String) stage1.get("subtitle"),
                                (String) stage1.get("content")
                        ),
                        new Card.StageInfo(
                                (String) stage2.get("title"),
                                (String) stage2.get("subtitle"),
                                (String) stage2.get("content")
                        ),
                        new Card.StageInfo(
                                (String) stage3.get("title"),
                                (String) stage3.get("subtitle"),
                                (String) stage3.get("content")
                        )
                );
            } catch (Exception e) {
                // JSON 파싱 실패시 기본값 사용
                return getDefaultCard();
            }
        }

        return getDefaultCard();
    }

    private Card getDefaultCard() {
        return new Card(
                new Card.StageInfo("임시 Stage1 Title", "Stage1 Subtitle", "Stage1 Content"),
                new Card.StageInfo("임시 Stage2 Title", "Stage2 Subtitle", "Stage2 Content"),
                new Card.StageInfo("임시 Stage3 Title", "Stage3 Subtitle", "Stage3 Content")
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