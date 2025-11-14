package com.peelie.friendship.domain;

import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileService;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FriendshipInfo {

    @Getter
    public static class CreateFriendship {
        private final String userName;
        private final String profileImageUrl;
        private final String instagramId;
        private final String stage1Bio;
        private final String stage2Bio;
        private final String stage3Bio;
        private final InteractionStyle interactionStyle;
        private final String levelOneInfo;
        private final String levelTwoInfo;
        private final String levelThreeInfo;

        public CreateFriendship(Profile profile) {
            this.userName = profile.getUserName();
            this.profileImageUrl = profile.getProfileImageUrl();
            this.instagramId = profile.getInstagramId();
            this.stage1Bio = profile.getStage1Bio();
            this.stage2Bio = profile.getStage2Bio();
            this.stage3Bio = profile.getStage3Bio();
            this.interactionStyle = profile.getInteractionStyle();
            this.levelOneInfo = profile.getStage1Bio();
            this.levelTwoInfo = profile.getStage2Bio();
            this.levelThreeInfo = profile.getStage3Bio();
        }
    }

    @Getter
    public static class FriendSummary {
        private final Long userId;
        private final String userName;
        private final InteractionStyle interactionStyle;
        private final String stage1Bio;
        private final String stage2Bio;
        private final String stage3Bio;
        private final Long stage;
        private final String profileImageUrl;

        public FriendSummary(Profile profile, Long stage) {
            this.userId = profile.getUserId();
            this.userName = profile.getUserName();
            this.interactionStyle = profile.getInteractionStyle();
            this.stage1Bio = profile.getStage1Bio();
            this.stage2Bio = profile.getStage2Bio();
            this.stage3Bio = profile.getStage3Bio();
            this.stage = stage;
            this.profileImageUrl = profile.getProfileImageUrl();
        }
    }
    // 친구 리스트 조회 응답 래퍼
    @Getter
    @Builder
    public static class FriendListResponse {
        private final List<FriendSummary> items;
    }

    // 랜덤 추천 5명 조회 응답 래퍼
    @Getter
    @Builder
    public static class RandomFriendResponse {
        private final List<FriendSummary> items;
    }

    @Getter
    public static class GetFriendDetail {
        private final Long userId;
        private final String userName;
        private final String profileImageUrl;
        private final String instagramId;
        private final List<ProfileInfo.BioInfo> bio;
        private final InteractionStyle interactionStyle;
        private final ProfileInfo.Card card;

        public GetFriendDetail(Profile profile) {
            this.userId = profile.getUserId();
            this.userName = profile.getUserName();
            this.profileImageUrl = profile.getProfileImageUrl();
            this.instagramId = profile.getInstagramId();

            //TODO: 재현님 카드 기능 완성 후 실제 데이터 반영
            this.bio = List.of(
                    new ProfileInfo.BioInfo("stage0", ProfileInfo.BioInfo.STAGE0_BIO),
                    new ProfileInfo.BioInfo("stage1", profile.getStage1Bio()),
                    new ProfileInfo.BioInfo("stage2", profile.getStage2Bio()),
                    new ProfileInfo.BioInfo("stage3", profile.getStage3Bio())
            );

            this.interactionStyle = profile.getInteractionStyle();

            this.card = new ProfileInfo.Card(
                    new ProfileInfo.Card.StageInfo("임시 Stage1 Title", "Stage1 Subtitle", "Stage1 Content"),
                    new ProfileInfo.Card.StageInfo("임시 Stage2 Title", "Stage2 Subtitle", "Stage2 Content"),
                    new ProfileInfo.Card.StageInfo("임시 Stage3 Title", "Stage3 Subtitle", "Stage3 Content")
            );
        }
    }




    }