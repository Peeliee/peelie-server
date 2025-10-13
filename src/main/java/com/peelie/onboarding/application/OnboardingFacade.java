package com.peelie.onboarding.application;

import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OnboardingFacade {
    private final OnboardingProcessService onboardingProcessService;

    public OnboardingInfo.Process startOnboarding(Long userId){
        return onboardingProcessService.startOnboarding(userId);
    }

    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command){
        return onboardingProcessService.selectCategories(command);
    }

    public OnboardingInfo.Process submitAnswer(OnboardingCommand.SubmitAnswer command){
        return onboardingProcessService.submitAnswer(command);
    }

    public OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command){
        return onboardingProcessService.submitInteractionStyle(command);
    }


}
