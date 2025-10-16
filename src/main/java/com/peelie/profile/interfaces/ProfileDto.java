package com.peelie.profile.interfaces;

import com.peelie.profile.domain.ProfileCommand;
import lombok.Getter;
import lombok.Setter;

public class ProfileDto {

    @Getter
    @Setter
    public static class RegisterProfileRequest {

        private Long userId;
        private String userName;
        private String instagramId;
        private String imageUrl;

        public ProfileCommand toCommand() {
            return ProfileCommand.builder()
                    .userId(userId)
                    .userName(userName)
                    .instagramId(instagramId)
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    public record UpdateProfileNameRequest(
            Long userId,
            String newName
    ) {}

    public record UpdateInstagramRequest(
            Long userId,
            String newInstagramId
    ) {}

    public record UpdateImageUrlRequest(
            Long userId,
            String newImgUrl
    ) {}

    public record resetProfileImageRequest(
            Long userId,
            String newImgUrl
    ) {}
}
