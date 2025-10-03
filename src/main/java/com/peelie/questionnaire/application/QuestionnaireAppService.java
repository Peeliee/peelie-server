package com.peelie.questionnaire.application;

import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireAppService {

   private final QuestionnaireService questionnaireService;

    public List<CategoryInfo> getAllCategories() {
        return questionnaireService.getAllCategories();
    }

    public CategoryInfo getL0QuestionAndChoicesById(Long categoryId) {
        return questionnaireService.getCategoryInfoById(categoryId);
    }

    public List<QuestionInfo> getL1ToL4QuestionsByIds(Long categoryId, Long subCategoryId) {
        return questionnaireService.getQuestionsByIds(categoryId, subCategoryId);
    }
}
