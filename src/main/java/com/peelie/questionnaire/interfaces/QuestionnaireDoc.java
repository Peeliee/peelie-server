package com.peelie.questionnaire.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Questionnaire", description = "온보딩 질문 API 명세")
public interface QuestionnaireDoc {

    @Operation(summary = "카테고리 전체 조회", description = "카테고리 전체 조회 API")
    SuccessResponse getCategories();

    @Operation(summary = "개별 카테고리 조회", description = "카테고리 개별 질문 및 하위 서브카테고리 조회")
    SuccessResponse<CategoryInfo> getCategoryQuestionAndChoices(
            @Parameter(description = "조회할 카테고리의 id", example = "1")
            @PathVariable Long categoryId);

    @Operation(summary = "서브카테고리 L1~L4 질문들 조회", description = "서브카테고리 L1~L4 질문들 조회")
    SuccessResponse<List<QuestionInfo>> getAllQuestions(
            @Parameter(description = "조회할 카테고리의 id", example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "조회할 서브카테고리의 id", example = "1")
            @PathVariable Long subCategoryId);
}
