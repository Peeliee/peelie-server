package com.peelie.profile.application;

import com.peelie.profile.domain.ProfileCommand;
import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileFacade {

    private final ProfileService profileService;

    // 프로필 생성
    public ProfileInfo registerProfile(ProfileCommand command) {
        return profileService.registerProfile(command);
    }

    // 프로필 조회
    public ProfileInfo getProfile(Long userId) {
        Long profileIdByUserId = profileService.getProfileIdByUserId(userId);
        return profileService.getProfile(profileIdByUserId);
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
