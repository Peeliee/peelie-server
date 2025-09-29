package com.peelie.questionnaire.domain;

import java.util.List;

public interface CategoryService {

    CategoryInfo.Main registerCategory(CategoryCommand.RegisterCategory command);
    List<CategoryInfo.Main> getAllCategories();
    CategoryInfo.Main getCategory(Long categoryId);
    CategoryInfo.Main registerQuestion(Long categoryId, CategoryCommand.RegisterQuestion command);
}