package com.peelie.onboarding.domain;


import com.peelie.onboarding.domain.card.CreateCardResponse;

public interface OnboardingProcessService {
    // OnboardingInfo.Process startOnboarding(Long userId);
    OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command);

    OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command);

    OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command);
    // 신규 카드 생성과 재생성은 같은 메서드 이용
    CreateCardResponse generateCard();

}
