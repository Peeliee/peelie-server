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
    public static class FriendDetailResponse {
        private Long userId;
        private String userName;
        private String profileImageUrl;
        private String instagramId;
        private String bio;
        private InteractionStyle interactionStyle;
        private Long stage;

        public static FriendDetailResponse from(FriendshipInfo.FriendDetail detail) {
            return FriendDetailResponse.builder()
                    .userId(detail.getUserId())
                    .userName(detail.getUserName())
                    .profileImageUrl(detail.getProfileImageUrl())
                    .instagramId(detail.getInstagramId())
                    .bio(detail.getBio())
                    .interactionStyle(detail.getInteractionStyle())
                    .stage(detail.getStage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FriendListResponse {
        private List<FriendDetailResponse> items;

        public static FriendListResponse from(FriendshipInfo.FriendListResponse info) {
            List<FriendDetailResponse> items = info.getItems().stream()
                    .map(FriendDetailResponse::from)
                    .toList();

            return FriendListResponse.builder()
                    .items(items)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RandomFriendResponse {
        private List<FriendDetailResponse> items;
        public static RandomFriendResponse from(FriendshipInfo.RandomFriendResponse info) {
            List<FriendDetailResponse> items = info.getItems().stream()
                    .map(FriendDetailResponse::from)
                    .toList();

            return RandomFriendResponse.builder()
                    .items(items)
                    .build();
        }
    }

    @Getter
    public static class CreateFriendshipRequest {
        private Long userId; // 요청 받는 아이디값

    }
}