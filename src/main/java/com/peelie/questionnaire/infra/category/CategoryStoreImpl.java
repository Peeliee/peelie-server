package com.peelie.questionnaire.infra.category;

import com.peelie.questionnaire.domain.category.Category;
import com.peelie.questionnaire.domain.category.CategoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryStoreImpl implements CategoryStore {

    private final CategoryRepository categoryRepository;

    @Override
    public Category store(Category category) {
        return categoryRepository.save(category);
    }
}
