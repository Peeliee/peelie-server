package com.peelie.questionnaire.infra;


import com.peelie.questionnaire.domain.Category;
import com.peelie.questionnaire.domain.CategoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryStoreImpl implements CategoryStore{

    private final CategoryRepository categoryRepository;

    @Override
    public Category store(Category category) {
        return categoryRepository.save(category);
    }
}
