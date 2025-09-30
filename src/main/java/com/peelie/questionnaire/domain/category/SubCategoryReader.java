package com.peelie.questionnaire.domain.category;

public interface SubCategoryReader {

    SubCategory getSubCategory(Long id);
    SubCategory getSubCategoryByName(String name);
}
