package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.question.ChoiceQuestion;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import lombok.Getter;

import java.util.List;

public class CategoryInfo {

    @Getter
    public static class Main {
        private final Long categoryId;
        private final String categoryName;
        private final List<QuestionInfo> questions;

        public Main(Category category) {
            this.categoryId = category.getId();
            this.categoryName = category.getCategoryName();

            List<QuestionInfo> questionInfoList = category.getChoiceQuestions().stream()
                    .map(QuestionInfo::new)
                    .toList();
            questionInfoList.add(new QuestionInfo(category.getL4Question()));

            this.questions = questionInfoList;
        }
    }

    @Getter
    public static class QuestionInfo {
        private final QuestionLevel level;
        private final String type;
        private final String content;
        private final List<String> options;

        public QuestionInfo(ChoiceQuestion question) {
            this.level = question.getLevel();
            this.type = "CHOICE";
            this.content = question.getContent();
            this.options = question.getOptions();
        }

        public QuestionInfo(String l4Content) {
            this.level = QuestionLevel.L4;
            this.type = "TEXT";
            this.content = l4Content;
            this.options = null;
        }
    }
}
