package com.peelie.profile.domain;

public interface ProfileService {
    ProfileInfo registerProfile(ProfileCommand.RegisterCommand command);
    ProfileInfo getProfile(Long profileId);
    Long getProfileIdByUserId(Long userId);
    void resetProfileImage(Long userId);
    void updateInteractionStyle(Long userId, String newInteractionStyle);
    ProfileInfo updateMyProfile(Long userId, ProfileCommand.UpdateCommand command);
}
