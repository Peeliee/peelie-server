package com.peelie.onboarding.infra;

import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnboardingStoreImpl implements OnboardingStore {

    private final OnboardingProcessRepository onboardingProcessRepository;

    @Override
    public OnboardingProcess store(OnboardingProcess onboardingProcess) {
        return onboardingProcessRepository.save(onboardingProcess);
    }
}