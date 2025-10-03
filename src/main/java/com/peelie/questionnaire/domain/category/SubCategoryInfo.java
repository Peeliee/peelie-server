package com.peelie.questionnaire.domain.category;

import lombok.Getter;

@Getter
public class SubCategoryInfo {

    private final Long id;
    private final String name;

    public SubCategoryInfo(SubCategory subCategory) {
        this.id = subCategory.getId();
        this.name = subCategory.getName();
    }
}
