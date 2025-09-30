package com.peelie.questionnaire.interfaces;

import com.peelie.common.response.SuccessResponse;
import com.peelie.questionnaire.application.QuestionnaireAppService;
import com.peelie.questionnaire.domain.CategoryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireAppService questionnaireAppService;

    @GetMapping
    public SuccessResponse getCategory(@RequestParam("category") String categoryName) {
        CategoryInfo.Main categoryResult = questionnaireAppService.retrieveCategory(categoryName);
        return SuccessResponse.ok(categoryResult);
    }
}
