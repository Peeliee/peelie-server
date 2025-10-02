package com.peelie.questionnaire.domain.category;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoryInfo {

    private final Long categoryId;
    private final String categoryName;
    private final String categoryQuestion;
    private final List<SubCategoryInfo> subCategoryNames;

    public CategoryInfo(Category category) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
        this.categoryQuestion = category.getCategoryQuestion();
        this.subCategoryNames = category.getSubCategories().stream()
                .map(SubCategoryInfo::new)
                .toList();
    }
}

