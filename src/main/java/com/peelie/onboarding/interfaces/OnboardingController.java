package com.peelie.onboarding.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.application.OnboardingFacade;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingFacade onboardingFacade;

    @PostMapping("/start/{userId}")
    public SuccessResponse<OnboardingInfo.Process> start(@PathVariable Long userId) {
        OnboardingInfo.Process result = onboardingFacade.startOnboarding(userId);
        return SuccessResponse.ok(result);
    }

    @PostMapping("/categories")
    public SuccessResponse<OnboardingInfo.Process> selectCategories(@RequestBody OnboardingCommand.SelectCategories command) {
        OnboardingInfo.Process result = onboardingFacade.selectCategories(command);
        return SuccessResponse.ok(result);
    }

    @PostMapping("/answers")
    public SuccessResponse<OnboardingInfo.Process> submitAnswer(@RequestBody OnboardingCommand.SubmitAnswer command) {
        OnboardingInfo.Process result = onboardingFacade.submitAnswer(command);
        return SuccessResponse.ok(result);
    }

    @PostMapping("/interaction")
    public SuccessResponse<OnboardingInfo.Process> submitInteractionStyle(@RequestBody OnboardingCommand.SubmitInteraction command) {
        OnboardingInfo.Process result = onboardingFacade.submitInteractionStyle(command);
        return SuccessResponse.ok(result);
    }

}
