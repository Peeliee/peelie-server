package com.peelie.profile.interfaces;

import com.peelie.profile.domain.InteractionStyle;
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

        public ProfileCommand.RegisterCommand toCommand(Long userId) {
            return ProfileCommand.RegisterCommand.builder()
                    .userId(userId)
                    .userName(userName)
                    .instagramId(instagramId)
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    public static class UpdateProfileRequest {

        private String userName;
        private String instagramId;
        private String imageUrl;
        private String interactionStyle;
        private String stage1Bio;
        private String stage2Bio;
        private String stage3Bio;

        public ProfileCommand.UpdateCommand toCommand() {
            return ProfileCommand.UpdateCommand.builder()
                    .userName(userName)
                    .instagramId(instagramId)
                    .imageUrl(imageUrl)
                    .interactionStyle(InteractionStyle.valueOf(interactionStyle))
                    .stage1Bio(stage1Bio)
                    .stage2Bio(stage2Bio)
                    .stage3Bio(stage3Bio)
                    .build();
        }
    }

}
