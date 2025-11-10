package com.peelie.onboarding.domain;

public interface OnboardingProcessService {
    //    OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);
    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);
    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);
    OnboardingInfo.CardGeneration initializeCard(OnboardingCommand.InitializeCard command);
}