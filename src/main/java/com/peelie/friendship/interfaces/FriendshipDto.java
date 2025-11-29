package com.peelie.friendship.interfaces;

import com.peelie.friendship.domain.FriendshipInfo;
import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.ProfileInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FriendshipDto {

    @Getter
    @Builder
    public static class FriendDetail { //친구 관계 즉시 형성, 친구 상세 조회
        private Long userId;
        private String userName;
        private String profileImageUrl;
        private String instagramId;
        private String bio;
        private InteractionStyle interactionStyle;
        private Long stage;
        private ProfileInfo.Card card;

        public static FriendDetail from(FriendshipInfo.FriendDetail detail) {
            String maskedImage = detail.getStage() <= 1 ? null : detail.getProfileImageUrl();
            String maskedInstagramId = detail.getStage() <= 2 ? null : detail.getInstagramId();

            return FriendDetail.builder()
                    .userId(detail.getUserId())
                    .userName(detail.getUserName())
                    .profileImageUrl(maskedImage)
                    .instagramId(maskedInstagramId)
                    .bio(detail.getBio())
                    .interactionStyle(detail.getInteractionStyle())
                    .stage(detail.getStage())
                    .card(detail.getCard())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FriendList { //친구 리스트 조회, 랜덤 추천 친구 5명 조회
        private Long userId;
        private String userName;
        private String interactionStyle;
        private String bio;
        private Long stage;
        private String profileUrl;

        public static FriendList from(FriendshipInfo.FriendDetail detail) {
            Long stage = detail.getStage();
            String maskedImage = detail.getStage() <= 1 ? null : detail.getProfileImageUrl();

            return FriendList.builder()
                    .userId(detail.getUserId())
                    .userName(detail.getUserName())
                    .interactionStyle(detail.getInteractionStyle().name())
                    .bio(detail.getBio())
                    .stage(stage)
                    .profileUrl(maskedImage)
                    .build();
        }
    }

    public static List<FriendList> toListItems(List<FriendshipInfo.FriendDetail> infoList) {
        return infoList.stream()
                .map(FriendList::from)
                .toList();
    }



    @Getter
    public static class CreateFriendshipRequest {
        private Long userId; // 요청 받는 아이디값

    }

    @Getter
    @Builder
    public static class ExistsResponse {
        private boolean exists;

        public static ExistsResponse from(boolean exists) {
            return ExistsResponse.builder()
                    .exists(exists)
                    .build();
        }
    }
}