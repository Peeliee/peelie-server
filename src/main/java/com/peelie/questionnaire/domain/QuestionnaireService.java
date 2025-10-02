package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;

import java.util.List;

public interface QuestionnaireService {
    List<CategoryInfo> getAllCategories();
    CategoryInfo getCategoryInfoById(Long categoryId);
    List<QuestionInfo> getQuestionsByIds(Long categoryId, Long subCategoryId);
}
