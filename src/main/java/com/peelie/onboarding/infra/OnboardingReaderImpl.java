package com.peelie.onboarding.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class OnboardingReaderImpl implements OnboardingReader{

    private final OnboardingProcessRepository onboardingProcessRepository;

    @Override
    public OnboardingProcess findOnboardingProcessByUserId(Long userId) {
        return onboardingProcessRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(userId + "해당 유저 아이디가 존재하지 않습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return onboardingProcessRepository.findByUserId(userId).isPresent();
    }
}