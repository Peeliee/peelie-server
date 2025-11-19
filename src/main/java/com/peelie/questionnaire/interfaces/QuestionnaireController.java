package com.peelie.questionnaire.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.questionnaire.application.QuestionnaireAppService;
import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController implements QuestionnaireDoc{

    private final QuestionnaireAppService questionnaireAppService;

    @GetMapping("/categories")
    public SuccessResponse getCategories() {
        List<CategoryInfo> result = questionnaireAppService.getAllCategories();
        return SuccessResponse.ok(result);
    }

    @GetMapping("/categories/{categoryId}")
    public SuccessResponse<CategoryInfo> getCategoryQuestionAndChoices(@PathVariable Long categoryId) {
        CategoryInfo result = questionnaireAppService.getL0QuestionAndChoicesById(categoryId);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/categories/{categoryId}/subcategories/{subCategoryId}/questions")
    public SuccessResponse<List<QuestionInfo>> getAllQuestions(@PathVariable Long categoryId,
                                                                  @PathVariable Long subCategoryId) {
        List<QuestionInfo> result = questionnaireAppService.getL1ToL4QuestionsByIds(categoryId, subCategoryId);
        return SuccessResponse.ok(result);
    }
}
