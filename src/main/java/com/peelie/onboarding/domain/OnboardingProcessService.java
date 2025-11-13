package com.peelie.onboarding.domain;

public interface OnboardingProcessService {
    // OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);

    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);

    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);

    OnboardingInfo.CardGeneration initializeCard(OnboardingCommand.InitializeCard command);

    // * [신규] 2. 카드 생성 상태 조회 (폴링용)
    // * ServiceImpl의 상태 조회 메서드를 호출합니다.
    public OnboardingInfo.CardGeneration getCardGenerationStatus(Long userId);
}
