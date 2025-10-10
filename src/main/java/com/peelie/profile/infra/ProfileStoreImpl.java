package com.peelie.profile.infra;

import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileStoreImpl implements ProfileStore {

    private final ProfileRepository profileRepository;

    @Override
    public Profile store(Profile profile) {
        return profileRepository.save(profile);
    }
}
