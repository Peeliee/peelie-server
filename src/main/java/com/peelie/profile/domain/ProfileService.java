package com.peelie.profile.domain;

public interface ProfileService {
    ProfileInfo registerProfile(Long userId, String userName, String instagramId, String imageUrl);

    ProfileInfo getProfile(Long profileId);
    Long getProfileIdByUserId(Long userId);

    void updateProfileName(Long userId, String newUserName);
    void updateInstagramId(Long userId, String newInstagramId);
    void resetProfileImage(Long userId);
    void updateProfileImage(Long userId, String newProfileImageUrl);
}
