package com.peelie.onboarding.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.card.GeneratedCardPayload;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Onboarding", description = "온보딩 API 명세")
public interface OnboardingDoc {

    @Operation(summary = "온보딩 카테고리 선택", description = "사용자가 온보딩에서 관심 카테고리(최대 3개)를 선택합니다.")
    SuccessResponse<OnboardingInfo.Process> selectCategories(
            @RequestBody OnboardingCommand.SelectCategories command);

    @Operation(summary = "온보딩 서브카테고리 답변 제출", description = "사용자가 선택한 카테고리 3개의 L0(subcategory)와 L2~L4 응답을 제출합니다.")
    SuccessResponse<OnboardingInfo.Process> SubmitSubCategoryAnswers(
            @RequestBody OnboardingCommand.SubmitSubCategoryAnswers command
    );

    @Operation(summary = "교류성향 선택", description = "사용자가 본인의 교류 성향을 선택합니다.")
    SuccessResponse<OnboardingInfo.Process> submitInteractionStyle(
            @RequestBody OnboardingCommand.SubmitInteraction command
    );

    @Operation(
            summary = "온보딩 카드 생성 요청",
            description = "사용자의 온보딩 응답을 기반으로 3단계의 카드 생성을 비동기로 요청합니다. 카드 재생성도 같은 API를 이용")
    SuccessResponse<GeneratedCardPayload> generateCard();

}
