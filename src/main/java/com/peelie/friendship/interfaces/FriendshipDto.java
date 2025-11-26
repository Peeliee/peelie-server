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
        private ProfileInfo.Card card;

        public static FriendDetailResponse from(FriendshipInfo.FriendDetail detail) {
            String maskedImage = detail.getStage() <= 1 ? null : detail.getProfileImageUrl();
            String maskedInstagramId = detail.getStage() <= 2 ? null : detail.getInstagramId();


            return FriendDetailResponse.builder()
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

    public static List<FriendDetailResponse> mapToDetailList(List<FriendshipInfo.FriendDetail> infoList) {
        return infoList.stream()
                .map(FriendDetailResponse::from)
                .toList();
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

    public static RandomFriendResponse from(FriendshipInfo.RandomFriendResponse info) {
        return RandomFriendResponse.builder()
                .items(mapToDetailList(info.getItems()))
                .build();
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