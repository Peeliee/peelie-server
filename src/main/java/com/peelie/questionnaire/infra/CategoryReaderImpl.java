package com.peelie.questionnaire.infra;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.Category;
import com.peelie.questionnaire.domain.CategoryReader;
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
                .orElseThrow(() -> new BaseException("카테고리가 존재하지 않습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
