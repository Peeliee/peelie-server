package com.peelie.friendship.domain;

import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FriendshipInfo {

    // 친구 리스트 조회 응답 래퍼
    @Getter
    @Builder
    public static class FriendListResponse {
        private final List<FriendDetail> items;
    }

    // 랜덤 추천 5명 조회 응답 래퍼
    @Getter
    @Builder
    public static class RandomFriendResponse {
        private final List<FriendDetail> items;
    }

    @Getter
    public static class FriendDetail {
        private final Long userId;
        private final String userName;
        private final String profileImageUrl;
        private final String instagramId;
        private final String bio;
        private final InteractionStyle interactionStyle;
        private final ProfileInfo.Card card;
        private final Long stage;

        public FriendDetail(Profile profile, FriendShipStage stage) {
            this.userId = profile.getUserId();
            this.userName = profile.getUserName();
            this.profileImageUrl = profile.getProfileImageUrl();
            this.instagramId = profile.getInstagramId();

            //TODO: 재현님 카드 기능 완성 후 실제 데이터 반영
            this.bio = switch (stage) {
                case STAGE_0 -> ProfileInfo.BioInfo.STAGE0_BIO;
                case STAGE_1 -> profile.getStage1Bio();
                case STAGE_2 -> profile.getStage2Bio();
                case STAGE_3 -> profile.getStage3Bio();
            };

            this.interactionStyle = profile.getInteractionStyle();

            this.card = new ProfileInfo.Card(
                    new ProfileInfo.Card.StageInfo("임시 Stage1 Title", "Stage1 Subtitle", "Stage1 Content"),
                    new ProfileInfo.Card.StageInfo("임시 Stage2 Title", "Stage2 Subtitle", "Stage2 Content"),
                    new ProfileInfo.Card.StageInfo("임시 Stage3 Title", "Stage3 Subtitle", "Stage3 Content")
            );

            this.stage = (long) stage.ordinal();
        }
    }
}
