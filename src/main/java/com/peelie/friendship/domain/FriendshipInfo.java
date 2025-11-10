package com.peelie.friendship.domain;

import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.ProfileInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FriendshipInfo {

    @Getter
    @Builder
    public static class CreateFriendship {
        private final String userName;
        private final String profileImageUrl;
        private final String instagramId;
        private final String bio;
        private final InteractionStyle interactionStyle;
        private final String levelOneInfo;
        private final String levelTwoInfo;
        private final String levelThreeInfo;

    }

    @Getter
    @Builder
    public static class GetFriendList { //랜덤 조회도 동일
        private final Long userId;
        private final String userName;
        private final InteractionStyle interactionStyle;
        private final String bio;
        private final Long stage;
        private final String profileImageUrl;
    }

    @Getter
    public static class getFriendDetail {
        private final Long userId;
        private final String userName;
        private final String profileImageUrl;
        private final String instagramId;
        private final String bio;
        private final InteractionStyle interactionStyle;
        private final ProfileInfo.Card card;

//        public GetFriendDetail(Profile profile) {
//            this.userId = profile.getUserId();
//            this.userName = profile.getUserName();
//            this.profileImageUrl = profile.getProfileImageUrl();
//            this.instagramId = profile.getInstagramId();
//            this.bio = profile.getBio();
//            this.interactionStyle = profile.getInteractionStyle();
//
//            this.card = new ProfileInfo.Card(
//                    new ProfileInfo.Card.StageInfo("임시 Stage1 Title", "Stage1 Subtitle", "Stage1 Content"),
//                    new ProfileInfo.Card.StageInfo("임시 Stage2 Title", "Stage2 Subtitle", "Stage2 Content"),
//                    new ProfileInfo.Card.StageInfo("임시 Stage3 Title", "Stage3 Subtitle", "Stage3 Content")
//            );
    }





    }