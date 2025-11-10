package com.peelie.onboarding.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.ErrorResponse;
import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.application.OnboardingFacade;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/card/initialize")
    public ResponseEntity<?> initializeCard(@RequestBody OnboardingCommand.InitializeCard command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        OnboardingInfo.CardGeneration result = onboardingFacade.initializeCard(cmd);

        // 정상 완료: GPT 응답 성공 → CREATED(201)
        if ("DONE".equals(result.getGenerationStatus())) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(SuccessResponse.created(result));
        }

        // TODO: 향후 실패 응답 포맷(ErrorResponse → CustomResponse로 통합) 리팩터링 예정
        String reasonJson = """
                {"generationStatus":"FAILED"}
                """;
        return ResponseEntity.ok(
                ErrorResponse.of(HttpStatus.OK.value(),
                        "GPT 호출 중 오류가 발생했습니다.",
                        null,
                        reasonJson)
        );
    }
}
