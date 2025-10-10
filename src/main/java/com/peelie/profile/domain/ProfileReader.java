package com.peelie.profile.domain;

public interface ProfileReader {
    Profile getProfile(Long id);
    Profile getProfileByUserId(Long userId);
}
