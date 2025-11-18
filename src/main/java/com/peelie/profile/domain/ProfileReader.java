package com.peelie.profile.domain;

import java.util.List;

public interface ProfileReader {
    Profile getProfile(Long id);
    Profile getProfileByUserId(Long userId);
    List<Profile> getProfilesByUserIds(List<Long> userIds);
}
