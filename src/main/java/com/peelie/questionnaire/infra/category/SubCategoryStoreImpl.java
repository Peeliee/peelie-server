package com.peelie.questionnaire.infra.category;

import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCategoryStoreImpl implements SubCategoryStore {

    private final SubCategoryRepository subCategoryRepository;

    @Override
    public SubCategory store(SubCategory subCategory) {
        return subCategoryRepository.save(subCategory);
    }
}
