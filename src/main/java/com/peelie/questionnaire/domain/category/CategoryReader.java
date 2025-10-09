package com.peelie.questionnaire.domain.category;

import java.util.List;

public interface CategoryReader {
    Category getCategory(Long id);
    Category getCategoryByName(String categoryName);
    List<Category> getAllCategories();
}
