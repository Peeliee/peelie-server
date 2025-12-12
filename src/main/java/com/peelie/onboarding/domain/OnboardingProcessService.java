package com.peelie.onboarding.domain;

import java.util.concurrent.CompletableFuture;

public interface OnboardingProcessService {
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);

    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);

    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);

    CompletableFuture<GeneratedCardBio> generateCardBio(String userPrompt);
}
