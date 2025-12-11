package com.peelie.onboarding.application;

import com.peelie.onboarding.domain.GeneratedCardBio;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingProcessService;
import com.peelie.profile.domain.ProfileCommand;
import com.peelie.profile.domain.ProfileService;
import com.peelie.prompt.PromptGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingFacade {

    private final OnboardingProcessService onboardingProcessService;
    private final ProfileService profileService;
    private final PromptGenerator promptGenerator;

    public OnboardingInfo.Process selectCategories(OnboardingCommand.SelectCategories command) {
        return onboardingProcessService.selectCategories(command);
    }

    public OnboardingInfo.Process submitSubCategoryAnswers(OnboardingCommand.SubmitSubCategoryAnswers command){
        return onboardingProcessService.submitSubCategoryAnswers(command);
    }

    public OnboardingInfo.Process submitInteractionStyle(OnboardingCommand.SubmitInteraction command) {
        profileService.updateInteractionStyle(command.getUserId(), command.getInteractionStyle());
        return onboardingProcessService.submitInteractionStyle(command);
    }

    // TODO: 예외 처리 게선 고민해보기
    public void generateInitCards(Long userId) {
        String userPrompt = promptGenerator.generatePrompt(userId);
        onboardingProcessService.generateCardBio(userPrompt)
                .thenAccept(generatedBio -> {
                    ProfileCommand.ApplyOnboardingResult command = GeneratedCardBio.toApplyOnboardingResult(generatedBio);
                    profileService.applyOnboarding(userId, command);
                }).exceptionally(ex -> {
            log.error("AI 생성 중 오류 발생: {}", ex.getMessage());
            return null;
        });
    }
}
