package com.peelie.onboarding.application;

import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingProcessService;
import com.peelie.onboarding.domain.card.CreateCardResponse;
import com.peelie.onboarding.domain.card.GetCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public CreateCardResponse initializeCard(OnboardingCommand.InitializeCard command) {
        return onboardingProcessService.initializeCard(command);
    }

    public CreateCardResponse regenerateCard(OnboardingCommand.RegenerateCard command) {
        return onboardingProcessService.regenerateCard(command);
    }
    public GetCardResponse getCard(Long userId) {
        return onboardingProcessService.getCard(userId);
    }
}
