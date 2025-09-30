package com.peelie.questionnaire.domain.category;

import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.Getter;

import java.util.List;

@Getter
public class SubCategoryInfo {

    private final Long subCategoryId;
    private final String categoryName;
    private final String SubCategoryName;
    private final List<QuestionInfo> questions;

    public SubCategoryInfo(SubCategory subCategory) {

        this.subCategoryId = subCategory.getId();
        this.categoryName = subCategory.getCategory().getName();
        this.SubCategoryName = subCategory.getName();
        this.questions = subCategory.getQuestions().stream()
                .map(QuestionInfo::new)
                .toList();
    }
}
