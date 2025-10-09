package com.peelie.questionnaire.infra.category;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.category.Category;
import com.peelie.questionnaire.domain.category.CategoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryReaderImpl implements CategoryReader {

    private final CategoryRepository categoryRepository;

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(id + ": 해당 id 카테고리가 없습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new BaseException(categoryName + ": 해당 이름의 카테고리가 없습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
