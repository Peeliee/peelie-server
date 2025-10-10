package com.peelie.profile.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileReaderImpl implements ProfileReader {

    private final ProfileRepository profileRepository;

    @Override
    public Profile getProfile(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new BaseException(id + "해당 id 값의 프로필이 없습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public Profile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(userId + "해당 회원 id 값의 프로필이 없습니다", ErrorCode.NOT_FOUND));
    }
}
