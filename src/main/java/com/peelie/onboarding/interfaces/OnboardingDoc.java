package com.peelie.onboarding.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.onboarding.domain.OnboardingCommand;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.card.CreateCardResponse;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Onboarding", description = "온보딩 API 명세")
public interface OnboardingDoc {

    @Operation(summary = "온보딩 카테고리 선택", description = "사용자가 온보딩에서 관심 카테고리(최대 3개)를 선택합니다.")
    SuccessResponse<OnboardingInfo.Process> selectCategories(
            @RequestBody OnboardingCommand.SelectCategories command);

    @Operation(summary = "온보딩 서브카테고리 답변 제출", description = "사용자가 선택한 서브카테고리에 대해 L1~L4 온보딩 답변을 제출합니다.")
    SuccessResponse<OnboardingInfo.Process> SubmitSubCategoryAnswers(
            @RequestBody OnboardingCommand.SubmitSubCategoryAnswers command
    );

    @Operation(summary = "온보딩 인터랙션 스타일 선택", description = "사용자가 원하는 상호작용(카드/퀴즈 등) 스타일을 선택합니다.")
    SuccessResponse<OnboardingInfo.Process> submitInteractionStyle(
            @RequestBody OnboardingCommand.SubmitInteraction command
    );

    @Operation(
            summary = "온보딩 카드 최초 생성 요청",
            description = "사용자의 온보딩 응답을 기반으로 3단계의 카드 생성을 비동기로 요청합니다.")
    SuccessResponse<CreateCardResponse> initializeCard(
            @RequestBody OnboardingCommand.InitializeCard command
    );

}
