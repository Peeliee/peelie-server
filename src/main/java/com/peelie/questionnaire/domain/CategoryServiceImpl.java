package com.peelie.questionnaire.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.questionnaire.domain.question.ChoiceQuestion;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryReader categoryReader;
    private final CategoryStore categoryStore;

    @Override
    @Transactional
    public CategoryInfo.Main registerCategory(CategoryCommand.RegisterCategory command) {
        Category newCategory = new Category(command.getCategoryName());
        Category savedCategory = categoryStore.store(newCategory);
        return new CategoryInfo.Main(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryInfo.Main> getAllCategories() {
        List<Category> categories = categoryReader.getAllCategories();
        return categories.stream()
                .map(CategoryInfo.Main::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryInfo.Main getCategory(Long categoryId) {
        Category category = categoryReader.getCategory(categoryId);
        return new CategoryInfo.Main(category);
    }

    @Override
    @Transactional
    public CategoryInfo.Main registerQuestion(Long categoryId, CategoryCommand.RegisterQuestion command) {
        Category category = categoryReader.getCategory(categoryId);

        if (command.getLevel() == QuestionLevel.L4) {
            if (category.getL4Question() != null) {
                throw new BaseException("L4 질문은 이미 등록되어 있습니다.", ErrorCode.VALIDATION_ERROR);
            }
            if (command.getOptions() != null && !command.getOptions().isEmpty()) {
                throw new BaseException("L4 질문에는 선택지를 등록할 수 없습니다.", ErrorCode.VALIDATION_ERROR);
            }
            category.updateL4Question(command.getContent());
        } else {
            // L0~L3 객관식 질문 등록
            if (category.getChoiceQuestions().stream()
                    .anyMatch(q -> q.getLevel() == command.getLevel())) {
                throw new BaseException(command.getLevel() + " 레벨의 객관식 질문은 이미 등록되어 있습니다.", ErrorCode.VALIDATION_ERROR);
            }
            if (command.getOptions() == null || command.getOptions().size() != 4) {
                throw new BaseException("객관식 질문은 4개의 선택지를 가져야 합니다.", ErrorCode.VALIDATION_ERROR);
            }

            ChoiceQuestion newChoiceQuestion = ChoiceQuestion.builder()
                    .category(category)
                    .level(command.getLevel())
                    .content(command.getContent())
                    .options(command.getOptions())
                    .build();

            category.addChoiceQuestion(newChoiceQuestion);
        }
        Category savedCategory = categoryStore.store(category);
        return new CategoryInfo.Main(savedCategory);
    }
}