package com.peelie.onboarding.application;

import com.peelie.onboarding.domain.InitCardsResponse;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingFacade {
    private final OnboardingProcessService onboardingProcessService;

    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        return onboardingProcessService.selectCategories(command);
    }

    public OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command){
        return onboardingProcessService.submitSubCategoryAnswers(command);
    }

    public OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command) {
        return onboardingProcessService.submitInteractionStyle(command);
    }

    public InitCardsResponse generateInitCards(Long userId) {
        return onboardingProcessService.generateInitCards(userId);
        // TODO: 여기에서 프로필에 저장하거나, 프로필에서 온보딩 서비스 코드를 호출할 지 추후 결정
    }
}
