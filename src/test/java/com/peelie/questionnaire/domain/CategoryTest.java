package com.peelie.questionnaire.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.questionnaire.domain.question.ChoiceQuestion;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("테스트 카테고리");
    }

    @Test
    void addChoiceQuestion() {
        // given
        ChoiceQuestion question = new ChoiceQuestion(
                category,
                QuestionLevel.L0,
                "L0 질문",
                new ArrayList<>(List.of("1", "2", "3", "4"))
        );

        // when
        category.addChoiceQuestion(question);

        // then
        assertThat(category.getChoiceQuestions()).hasSize(1);
        assertThat(category.getChoiceQuestions().get(0).getContent()).isEqualTo("L0 질문");
    }

    @Test
    void addChoiceQuestion_4개이상등록시예외() {
        // given
        for (int i = 0; i < 4; i++) {
            ChoiceQuestion question = new ChoiceQuestion(
                    category,
                    QuestionLevel.values()[i],
                    "질문 " + i,
                    new ArrayList<>(List.of("1", "2", "3", "4"))
            );
            category.addChoiceQuestion(question);
        }

        ChoiceQuestion extra = new ChoiceQuestion(
                category,
                QuestionLevel.L3,
                "추가 질문",
                new ArrayList<>(List.of("1", "2", "3", "4"))
        );

        // when // then
        assertThatThrownBy(() -> category.addChoiceQuestion(extra))
                .isInstanceOf(BaseException.class)
                .hasMessage("객관식 질문은 4개까지 등록 가능합니다.");
    }

    @Test
    void updateL4Question() {
        // given
        String l4 = "서술형 질문입니다";

        // when
        category.updateL4Question(l4);

        // then
        assertThat(category.getL4Question()).isEqualTo(l4);
    }

    @Test
    void getCategoryName() {
        // when
        String name = category.getCategoryName();

        // then
        assertThat(name).isEqualTo("테스트 카테고리");
    }

    @Test
    void 출력_테스트() {
        System.out.println(category);

        for (int i = 0; i < 4; i++) {
            ChoiceQuestion question = new ChoiceQuestion(
                    category,
                    QuestionLevel.values()[i],
                    "질문 " + i,
                    new ArrayList<>(List.of("선택지1", "2", "3", "4"))
            );
            category.addChoiceQuestion(question);
        }
        category.updateL4Question("질문 4");

        CategoryInfo.Main main = new CategoryInfo.Main(category);
        System.out.println(main.getQuestions());
    }
}