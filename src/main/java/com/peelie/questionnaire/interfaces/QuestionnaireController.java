package com.peelie.questionnaire.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.questionnaire.application.QuestionnaireAppService;
import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireAppService questionnaireAppService;

    @GetMapping("/category")
    public SuccessResponse getCategory(@RequestParam("category") String categoryName) {
        CategoryInfo category = questionnaireAppService.getCategory(categoryName);
        return SuccessResponse.ok(category);
    }

    @GetMapping("/questions")
    public SuccessResponse getQuestions(@RequestParam("subcategory") String subCategoryName) {
        List<QuestionInfo> subCategory = questionnaireAppService.getSubCategory(subCategoryName);
        return SuccessResponse.ok(subCategory);
    }

    @GetMapping("/question/l1")
    public SuccessResponse getL1Question(@RequestParam("subcategory") String subCategoryName) {
        QuestionInfo result = questionnaireAppService.getL1Question(subCategoryName);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/question/l2")
    public SuccessResponse getL2Question(@RequestParam("subcategory") String subCategoryName) {
        QuestionInfo result = questionnaireAppService.getL2Question(subCategoryName);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/question/l3")
    public SuccessResponse getL3Question(@RequestParam("subcategory") String subCategoryName) {
        QuestionInfo result = questionnaireAppService.getL3Question(subCategoryName);
        return SuccessResponse.ok(result);
    }

    @GetMapping("/question/l4")
    public SuccessResponse getL4Question(@RequestParam("subcategory") String subCategoryName) {
        QuestionInfo result = questionnaireAppService.getL4Question(subCategoryName);
        return SuccessResponse.ok(result);
    }
}
