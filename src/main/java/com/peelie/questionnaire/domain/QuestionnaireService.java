package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.category.SubCategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;

import java.util.List;

public interface QuestionnaireService {
    List<CategoryInfo> getAllCategories();
    CategoryInfo getCategory(Long categoryId);
    CategoryInfo getCategoryByName(String categoryName);

    List<SubCategoryInfo> getSubCategories(Long categoryId);
    SubCategoryInfo getSubCategory(Long subCategoryId);
    SubCategoryInfo getSubCategoryByName(String subCategoryName);

    QuestionInfo getL1Question(String subCategoryName);
    QuestionInfo getL2Question(String subCategoryName);
    QuestionInfo getL3Question(String subCategoryName);
    QuestionInfo getL4Question(String subCategoryName);

}
