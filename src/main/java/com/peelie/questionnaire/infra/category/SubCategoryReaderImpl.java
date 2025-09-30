package com.peelie.questionnaire.infra.category;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCategoryReaderImpl implements SubCategoryReader {

    private final SubCategoryRepository subCategoryRepository;

    @Override
    public SubCategory getSubCategory(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new BaseException(id + "해당 Id 값의 서브카테고리가 존재하지 않습니다", ErrorCode.NOT_FOUND));
    }

    @Override
    public SubCategory getSubCategoryByName(String name) {
        return subCategoryRepository.findByName(name)
                .orElseThrow(() -> new BaseException(name + "해당 이름의 서브카테고리가 존재하지 않습니다", ErrorCode.NOT_FOUND));
    }
}
