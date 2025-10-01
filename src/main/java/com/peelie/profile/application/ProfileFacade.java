package com.peelie.profile.application;

import com.peelie.profile.domain.ProfileInfo;
import com.peelie.profile.domain.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileFacade {

    private final ProfileService profileService;

    // TODO userId 받도록 수정
    public ProfileInfo registerProfile(String userName, String instagramId, String imageUrl) {
        return profileService.registerProfile(userName, instagramId, imageUrl);
    }

    public ProfileInfo getProfile(Long profileId) {
        return profileService.getProfile(profileId);
    }
}
