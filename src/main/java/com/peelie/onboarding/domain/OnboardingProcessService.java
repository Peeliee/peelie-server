package com.peelie.onboarding.domain;

import com.peelie.onboarding.domain.card.CreateCardResponse;
import com.peelie.onboarding.domain.card.GetCardResponse;

public interface OnboardingProcessService {
    // OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);

    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);

    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);
    // [신규] 1. 카드 생성용
    CreateCardResponse initializeCard(OnboardingCommand.InitializeCard command);
    // [신규] 2. 카드 생성 상태 조회 (폴링용)
    GetCardResponse getCard(Long userId);
}
