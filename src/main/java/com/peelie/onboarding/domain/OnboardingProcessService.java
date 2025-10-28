package com.peelie.onboarding.domain;

public interface OnboardingProcessService {
    OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);
    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);
    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);
}
//Todo submitInteractionStyle 한줄소개도 포함하는 이름으로 추후 변경 필요
