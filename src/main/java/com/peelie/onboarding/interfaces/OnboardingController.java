package com.peelie.onboarding.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.application.OnboardingFacade;
import com.peelie.onboarding.domain.card.CardInfo;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.card.CreateCardResponse;
import com.peelie.onboarding.domain.card.GetCardResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController implements OnboardingDoc {

    private final OnboardingFacade onboardingFacade;

    @PutMapping("/categories")
    public SuccessResponse<OnboardingInfo.Process> selectCategories(
            @RequestBody OnboardingCommand.SelectCategories command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.selectCategories(cmd);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/answers")
    public SuccessResponse<OnboardingInfo.Process> SubmitSubCategoryAnswers(
            @RequestBody OnboardingCommand.SubmitSubCategoryAnswers command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.submitSubCategoryAnswers(cmd);
        return SuccessResponse.ok(result);
    }

    @PutMapping("/interaction")
    public SuccessResponse<OnboardingInfo.Process> submitInteractionStyle(
            @RequestBody OnboardingCommand.SubmitInteraction command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.submitInteractionStyle(cmd);
        return SuccessResponse.ok(result);
    }

    @PostMapping("/card/initialize")
    public SuccessResponse<CreateCardResponse> initializeCard(@RequestBody OnboardingCommand.InitializeCard command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.initializeCard(cmd);
        //202 accepted 상태 코드 사용해 비동기 작업 요청임을 표시
        return SuccessResponse.ok(result);
    }

    @PostMapping("/card/regenerate")
    public SuccessResponse<CreateCardResponse> regenerateCard(@RequestBody OnboardingCommand.RegenerateCard command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        var result = onboardingFacade.regenerateCard(cmd);
        //202 accepted 상태 코드 사용해 비동기 작업 요청임을 표시
        return SuccessResponse.ok(result);
    }
    @GetMapping("/card/status")
    public SuccessResponse<GetCardResponse> getCardGenerationStatus() {
        Long userId = UserContextHolder.getUserId();
        var result = onboardingFacade.getCard(userId);
        return SuccessResponse.ok(result);
    }
}
