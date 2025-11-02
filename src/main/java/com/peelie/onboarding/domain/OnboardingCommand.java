package com.peelie.onboarding.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    public static class SubmitSubCategoryAnswers {
        private Long userId;
        private Long categoryId;
        private Long subCategoryId;
        private List<LevelAnswerCommand> answers;

        @Getter
        @Builder
        @ToString
        @NoArgsConstructor
        public static class LevelAnswerCommand{
            private String level;
            private Long optionId;
            private String textAnswer;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class SubmitInteractionBio {
        private Long userId;
        private String interactionStyle;
        private String bio;
    }

}