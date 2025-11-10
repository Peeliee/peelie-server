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
    public ProfileInfo registerProfile(ProfileCommand.RegisterCommand command) {
        return profileService.registerProfile(command);
    }

    // 프로필 조회
    public ProfileInfo getMyProfile(Long userId) {
        Long profileIdByUserId = profileService.getProfileIdByUserId(userId);
        return profileService.getProfile(profileIdByUserId);
    }

    // 프로필 수정
    // 프로필 이름 수정
    public ProfileInfo updateProfile(Long userId, ProfileCommand.UpdateCommand command) {
        return profileService.updateMyProfile(userId, command);
    }

    // 프로필 사진 리셋
    public void resetProfileImage(Long userId) {
        profileService.resetProfileImage(userId);
    }
}
