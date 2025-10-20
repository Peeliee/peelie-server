package com.peelie.profile.interfaces;

import com.peelie.profile.domain.ProfileCommand;
import lombok.Getter;
import lombok.Setter;

public class ProfileDto {

    @Getter
    @Setter
    public static class RegisterProfileRequest {

        private String userName;
        private String instagramId;
        private String imageUrl;

        public ProfileCommand toCommand(Long userId) {
            return ProfileCommand.builder()
                    .userId(userId)
                    .userName(userName)
                    .instagramId(instagramId)
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    public record UpdateProfileNameRequest(
            String newName
    ) {}

    public record UpdateInstagramRequest(
            String newInstagramId
    ) {}

    public record UpdateImageUrlRequest(
            String newImgUrl
    ) {}

    public record resetProfileImageRequest(
            String newImgUrl
    ) {}
}
