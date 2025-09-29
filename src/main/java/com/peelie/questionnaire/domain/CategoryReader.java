package com.peelie.questionnaire.domain;


import java.util.List;

public interface CategoryReader {
    Category getCategory(Long id);
    List<Category> getAllCategories();
}
