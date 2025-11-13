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
    public ResponseEntity<?> initializeCard(@RequestBody OnboardingCommand.InitializeCard command) {
        Long userId = UserContextHolder.getUserId();
        var cmd = command.withUserId(userId);
        OnboardingInfo.CardGeneration result = onboardingFacade.initializeCard(cmd);

        // // 정상 완료: GPT 응답 성공 → CREATED(201)
        // if ("DONE".equals(result.getGenerationStatus())) {
        // return ResponseEntity
        // .status(HttpStatus.CREATED)
        // .body(SuccessResponse.created(result));
        // }
        //
        // // TODO: 향후 실패 응답 포맷(ErrorResponse → CustomResponse로 통합) 리팩터링 예정
        // String reasonJson = """
        // {"generationStatus":"FAILED"}
        // """;
        // return ResponseEntity.ok(
        // ErrorResponse.of(HttpStatus.OK.value(),
        // "GPT 호출 중 오류가 발생했습니다.",
        // null,
        // reasonJson));
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(SuccessResponse.of(202, "Generation started", result));
    }



    @GetMapping("/card/status")
    public ResponseEntity<?> getCardGenerationStatus() {
        Long userId = UserContextHolder.getUserId();
        var cmd = OnboardingCommand.GetCardStatus.builder().build().withUserId(userId);
        OnboardingInfo.CardGeneration statusResult = onboardingFacade.getCardGenerationStatus(cmd);

        // if (statusResult == null) {
        // return ResponseEntity
        // .status(HttpStatus.NOT_FOUND)
        // .body(ErrorResponse.of(HttpStatus.401, "No task found for user.",
        // "NO_TASK"));
        // }
        String status = statusResult.getGenerationStatus();

        switch (status) {
            case "DONE":
                // [성공] 200 OK
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(
                                SuccessResponse.ok(statusResult));
            case "GENERATING":
                // [진행 중] 202 ACCEPTED
                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(SuccessResponse.ok(statusResult));

            case "FAILED":
            default:
                // [실패] 500 Internal Server Error
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ErrorResponse.of(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Generation failed.",
                                null,
                                "카드가 아직 생성되지 않았습니다."));
        }
    }
}
