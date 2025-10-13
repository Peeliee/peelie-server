package com.peelie.onboarding.domain;

public interface OnboardingReader {
    OnboardingProcess findOnboardingProcessByUserId(Long userId);
    //기본 아이디로 찾을 일이 있을지 의문
    boolean existsByUserId(Long userId);
}
