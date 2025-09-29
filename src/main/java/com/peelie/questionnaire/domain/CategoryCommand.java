package com.peelie.questionnaire.domain;

import com.peelie.questionnaire.domain.question.QuestionLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public class CategoryCommand {

    @Getter
    @Builder
    @ToString
    public static class RegisterCategory {
        private String categoryName;
    }

    @Getter
    @Builder
    @ToString
    public static class RegisterQuestion {
        private QuestionLevel level; // L0, L1, L2, L3, L4
        private String content;
        private List<String> options; // L0~L3 객관식 질문에만 사용, L4는 null
    }

    @Getter
    @Builder
    @ToString
    public static class UpdateQuestion {
        private Long questionId;
        private QuestionLevel level;
        private String content;
        private List<String> options;
    }
}
