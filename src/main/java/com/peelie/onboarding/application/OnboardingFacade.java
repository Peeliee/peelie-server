package com.peelie.onboarding.application;

import com.peelie.onboarding.domain.CardInfo;
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

    public CardInfo.Stage initializeCard(OnboardingCommand.InitializeCard command) {
        return onboardingProcessService.initializeCard(command);
    }

//    public CardInfo.Stage getCardGenerationStatus(Long userId) {
//        return onboardingProcessService.getCardGenerationStatus(userId);
//    }
}
