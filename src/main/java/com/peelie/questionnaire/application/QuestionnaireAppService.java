package com.peelie.questionnaire.application;

import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.CategoryInfo;
import com.peelie.questionnaire.domain.category.SubCategoryInfo;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireAppService {

   private final QuestionnaireService questionnaireService;

   /*
    /*
    1. 카테고리 조회 -> 서브 카테고리 나옴
    2. 카테고리, 서브카테고리 이름 두개 입력 하면 -> L0~L4 질문 나옴
     */

    public CategoryInfo getCategory(String categoryName) {
        return questionnaireService.getCategoryByName(categoryName);
    }

    public List<QuestionInfo> getSubCategory(String subCategoryName) {
        SubCategoryInfo subCategoryInfo = questionnaireService.getSubCategoryByName(subCategoryName);
        return subCategoryInfo.getQuestions();
    }

    public QuestionInfo getL1Question(String subCategoryName) {
        return questionnaireService.getL1Question(subCategoryName);
    }

    public QuestionInfo getL2Question(String subCategoryName) {
        return questionnaireService.getL2Question(subCategoryName);
    }

    public QuestionInfo getL3Question(String subCategoryName) {
        return questionnaireService.getL3Question(subCategoryName);
    }

    public QuestionInfo getL4Question(String subCategoryName) {
        return questionnaireService.getL4Question(subCategoryName);
    }
}
