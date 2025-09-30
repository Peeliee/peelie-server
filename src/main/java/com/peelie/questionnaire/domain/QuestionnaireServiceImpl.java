package com.peelie.questionnaire.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.category.*;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireServiceImpl implements QuestionnaireService{

    private final CategoryReader categoryReader;
    private final SubCategoryReader subCategoryReader;

    @Override
    public List<CategoryInfo> getAllCategories() {
        List<Category> allCategories = categoryReader.getAllCategories();

        return allCategories.stream()
                .map(CategoryInfo::new)
                .toList();
    }

    @Override
    public CategoryInfo getCategory(Long categoryId) {
        Category category = categoryReader.getCategory(categoryId);

        return new CategoryInfo(category);
    }

    @Override
    public CategoryInfo getCategoryByName(String categoryName) {
        Category category = categoryReader.getCategoryByName(categoryName);

        return new CategoryInfo(category);
    }

    @Override
    public List<SubCategoryInfo> getSubCategories(Long categoryId) {
        Category category = categoryReader.getCategory(categoryId);
        List<SubCategory> subCategories = category.getSubCategories();

        return subCategories.stream()
                .map(SubCategoryInfo::new)
                .toList();
    }

    @Override
    public SubCategoryInfo getSubCategory(Long subCategoryId) {
        SubCategory subCategory = subCategoryReader.getSubCategory(subCategoryId);

        return new SubCategoryInfo(subCategory);
    }

    @Override
    public SubCategoryInfo getSubCategoryByName(String subCategoryName) {
        SubCategory subCategoryByName = subCategoryReader.getSubCategoryByName(subCategoryName);

        return new SubCategoryInfo(subCategoryByName);
    }

    @Override
    public QuestionInfo getL1Question(String subCategoryName) {
        SubCategory subCategory = subCategoryReader.getSubCategoryByName(subCategoryName);
        QuestionInfo questionInfo = subCategory.getQuestions().stream()
                .filter(question -> question.getLevel() == QuestionLevel.L1)
                .map(QuestionInfo::new)
                .findFirst()
                .orElseThrow(() -> new BaseException(subCategoryName + "해당 서브 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        return questionInfo;
    }

    @Override
    public QuestionInfo getL2Question(String subCategoryName) {
        SubCategory subCategory = subCategoryReader.getSubCategoryByName(subCategoryName);
        QuestionInfo questionInfo = subCategory.getQuestions().stream()
                .filter(question -> question.getLevel() == QuestionLevel.L2)
                .map(QuestionInfo::new)
                .findFirst()
                .orElseThrow(() -> new BaseException(subCategoryName + "해당 서브 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        return questionInfo;
    }

    @Override
    public QuestionInfo getL3Question(String subCategoryName) {
        SubCategory subCategory = subCategoryReader.getSubCategoryByName(subCategoryName);
        QuestionInfo questionInfo = subCategory.getQuestions().stream()
                .filter(question -> question.getLevel() == QuestionLevel.L3)
                .map(QuestionInfo::new)
                .findFirst()
                .orElseThrow(() -> new BaseException(subCategoryName + "해당 서브 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        return questionInfo;
    }

    @Override
    public QuestionInfo getL4Question(String subCategoryName) {
        SubCategory subCategory = subCategoryReader.getSubCategoryByName(subCategoryName);
        QuestionInfo questionInfo = subCategory.getQuestions().stream()
                .filter(question -> question.getLevel() == QuestionLevel.L4)
                .map(QuestionInfo::new)
                .findFirst()
                .orElseThrow(() -> new BaseException(subCategoryName + "해당 서브 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        return questionInfo;
    }
}
