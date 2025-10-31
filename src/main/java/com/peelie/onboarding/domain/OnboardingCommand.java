package com.peelie.onboarding.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

public class OnboardingCommand {

    @Getter
    @Builder
    @ToString
    public static class SelectCategories {
        private Long userId;
        private Set<Long> categoryIds;
    }

    @Getter
    @Builder
    @ToString
    public static class SubmitAnswer {
        private Long userId; //도메인 엔티티에는 포함되지 않지만 특정 사용자의 온보딩 프로세스를 식별하기 위해 요청 DTO 에서만 사용
        private Long questionId;
        private String value;

        private Long categoryId; //getQuestionsByIds의 매개변수로 필요함
        private Long subCategoryId;
    }

    @Getter
    @Builder
    @ToString
    public static class SubmitInteraction {
        private Long userId;
        private String interactionStyle;
        private String bio;
    }

    @Getter
    @Builder
    @ToString
    public static class InitializeCard {
        private Long userId;
        private List<Long> categoryIds;
        private Long subCategoryId;
        private List<AnswerData> answers;
    }

    @Getter
    @Builder
    @ToString
    public static class AnswerData {
        private String level; // "L1", "L2", "L3", "L4"
        private Long L1AnswerId;
        private Long L2AnswerId;
        private Long L3AnswerId;
        private String L4Answer;
    }

}