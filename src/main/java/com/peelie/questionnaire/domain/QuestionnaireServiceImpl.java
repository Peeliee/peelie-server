package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.category.*;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionnaireServiceImpl implements QuestionnaireService{

    private final CategoryReader categoryReader;
    private final SubCategoryReader subCategoryReader;

    @Override
    public List<CategoryInfo> getAllCategories() {
        List<Category> allCategories = categoryReader.getAllCategories();

        return allCategories.stream()
                .map(CategoryInfo::new)
                .toList();
    }

    @Override
    public CategoryInfo getCategoryInfoById(Long categoryId) {
        return new CategoryInfo(categoryReader.getCategory(categoryId));
    }

    @Override
    public List<QuestionInfo> getQuestionsByIds(Long categoryId, Long subCategoryId) {
        SubCategory subCategory = subCategoryReader.getSubCategoryByIds(categoryId, subCategoryId);

        return subCategory.getQuestions().stream()
                .map(QuestionInfo::new)
                .collect(Collectors.toList());
    }
}
