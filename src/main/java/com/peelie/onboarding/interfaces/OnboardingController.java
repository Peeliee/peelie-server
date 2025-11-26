package com.peelie.onboarding.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.ErrorResponse;
import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.application.OnboardingFacade;
import com.peelie.onboarding.domain.CardInfo;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.CardInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

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
    public SuccessResponse<CardInfo.Stage> initializeCard(@RequestBody OnboardingCommand.InitializeCard command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        CardInfo.Stage result = onboardingFacade.initializeCard(cmd);
        //202 accepted 상태 코드 사용해 비동기 작업 요청임을 표시
        return SuccessResponse.of(HttpStatus.ACCEPTED, "Generation started", result);
    }
// TODO: GET 구현 , facade에서 호출하는 부분도 일시적으로 주석 처리
    /*
    @GetMapping("/card/status")
    public ResponseEntity<?> getCardGenerationStatus() {
        Long userId = UserContextHolder.getUserId();
        var cmd = OnboardingCommand.GetCardStatus.builder().build().withUserId(userId);
        CardInfo.StageInfo statusResult = onboardingFacade.getCardGenerationStatus(cmd);

    }*/
}
