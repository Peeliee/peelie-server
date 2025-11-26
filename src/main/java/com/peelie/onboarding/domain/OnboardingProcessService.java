package com.peelie.onboarding.domain;

public interface OnboardingProcessService {
    // OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);

    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);

    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);
    // [신규] 1. 카드 생성용
    CardInfo.StageInfo initializeCard(OnboardingCommand.InitializeCard command);
    // [신규] 2. 카드 생성 상태 조회 (폴링용)
    CardInfo.StageInfo getCardGenerationStatus(Long userId);
}
