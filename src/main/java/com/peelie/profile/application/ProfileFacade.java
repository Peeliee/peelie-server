package com.peelie.profile.application;

import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileFacade {

    private final ProfileService profileService;

    // 프로필 생성
    // TODO userId 받도록 수정 (완료)
    public ProfileInfo registerProfile(Long userId, String userName, String instagramId, String imageUrl) {
        return profileService.registerProfile(userId, userName, instagramId, imageUrl);
    }

    // 프로필 조회
    public ProfileInfo getProfile(Long profileId) {
        return profileService.getProfile(profileId);
    }

    // 프로필 수정
    // 프로필 이름 수정
    public void updateProfileName(Long userId, String newProfileName) {
        profileService.updateProfileName(userId, newProfileName);
    }
    // 프로필 인스타 ID 수정
    public void updateInstagramId(Long userId, String newInstagramId) {
        profileService.updateInstagramId(userId, newInstagramId);
    }
    // 프로필 이미지 수정
    public void updateImageUrl(Long userId, String newImageUrl) {
        profileService.updateProfileImage(userId, newImageUrl);
    }

    // 프로필 사진 리셋
    public void resetProfileImage(Long userId) {
        profileService.resetProfileImage(userId);
    }
}
