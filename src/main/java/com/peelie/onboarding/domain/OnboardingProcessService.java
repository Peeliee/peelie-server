package com.peelie.onboarding.domain;

public interface OnboardingProcessService {
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);
}
