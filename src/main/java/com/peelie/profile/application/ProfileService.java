package com.peelie.profile.application;

import com.peelie.profile.domain.InteractionStyle;
import com.peelie.profile.domain.ProfileInfo;

import java.util.Set;

public interface ProfileService {
    void createInitialProfile(Long userId);
    void updateProfileFromOnboarding(Long userId, Set<Long> categoryIds, InteractionStyle style, String bio);
    ProfileInfo getProfile(Long userId);
}