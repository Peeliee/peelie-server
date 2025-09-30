package com.peelie.questionnaire.application;

import com.peelie.questionnaire.domain.CategoryInfo;
import com.peelie.questionnaire.domain.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionnaireAppService {

    private final CategoryService categoryService;

    public CategoryInfo.Main retrieveCategory(Long id) {
        return categoryService.getCategory(id);
    }
}
