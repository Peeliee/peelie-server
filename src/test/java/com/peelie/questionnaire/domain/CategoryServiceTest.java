package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.question.ChoiceQuestion;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryStore categoryStore;

    @BeforeEach
    void setUp() {
        Category category1 = new Category("테스트 카테고리1");

        for (int i = 0; i < 4; i++) {
            ChoiceQuestion question = new ChoiceQuestion(
                    category1,
                    QuestionLevel.values()[i],
                    "카테고리 1의 질문 " + i,
                    new ArrayList<>(List.of("선택지1", "선택지2", "선택지3", "선택지4"))
            );
            category1.addChoiceQuestion(question);
        }

        category1.updateL4Question("카테고리 1의 질문 4");

        Category category2 = new Category("테스트 카테고리2");

        categoryStore.store(category1);
        categoryStore.store(category2);
    }

    @Test
    void 전체조회() {
        //then
        assertThat(categoryService.getAllCategories().size()).isEqualTo(2);
        assertThat(categoryService.getAllCategories().get(0).getQuestions().size()).isEqualTo(5);
        assertThat(categoryService.getAllCategories().get(1).getQuestions().size()).isEqualTo(1);
    }

    @Test
    void 단일조회() {
        assertThat(categoryService.getCategory(1L).getCategoryName()).isEqualTo("테스트 카테고리1");

        assertThat(categoryService.getCategory(1L).getQuestions().get(0).getContent())
                .isEqualTo("카테고리 1의 질문 0");
        assertThat(categoryService.getCategory(1L).getQuestions().get(0).getOptions().size())
                .isEqualTo(4);

        assertThat(categoryService.getCategory(1L).getQuestions().get(1).getContent())
                .isEqualTo("카테고리 1의 질문 1");
        assertThat(categoryService.getCategory(1L).getQuestions().get(1).getOptions().size())
                .isEqualTo(4);

        assertThat(categoryService.getCategory(1L).getQuestions().get(2).getContent())
                .isEqualTo("카테고리 1의 질문 2");
        assertThat(categoryService.getCategory(1L).getQuestions().get(2).getOptions().size())
                .isEqualTo(4);

        assertThat(categoryService.getCategory(1L).getQuestions().get(3).getContent())
                .isEqualTo("카테고리 1의 질문 3");
        assertThat(categoryService.getCategory(1L).getQuestions().get(3).getOptions().size())
                .isEqualTo(4);

        assertThat(categoryService.getCategory(1L).getQuestions().getLast().getContent())
                .isEqualTo("카테고리 1의 질문 4");

        assertThat(categoryService.getCategory(2L).getCategoryName()).isEqualTo("테스트 카테고리2");
    }
}