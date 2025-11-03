package com.peelie.onboarding.interfaces;

import com.peelie.common.context.UserContextHolder;
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


    @PutMapping ("/categories")
    public SuccessResponse<OnboardingInfo.Process> selectCategories(@RequestBody OnboardingCommand.SelectCategories command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.selectCategories(cmd);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/answers")
    public SuccessResponse<OnboardingInfo.Process> SubmitSubCategoryAnswers(@RequestBody OnboardingCommand.SubmitSubCategoryAnswers command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.submitSubCategoryAnswers(cmd);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/interaction")
    public SuccessResponse<OnboardingInfo.Process> submitInteractionStyle(@RequestBody OnboardingCommand.SubmitInteraction command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.submitInteractionStyle(cmd);
        return SuccessResponse.ok(result);
    }

}
