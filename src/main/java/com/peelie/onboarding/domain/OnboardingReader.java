package com.peelie.onboarding.domain;

public interface OnboardingReader {
    OnboardingProcess findOnboardingProcessByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
