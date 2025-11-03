package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;
import java.util.Set;

public class OnboardingCommand {

    @Getter
    @Builder
    @ToString
    public static class SelectCategories {
        @JsonIgnore
        private Long userId;

        private Set<Long> categoryIds;

        public SelectCategories withUserId(Long userId) {
            this.userId = userId;
            return this;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class SubmitSubCategoryAnswers {
        @JsonIgnore
        private Long userId;

        private Long categoryId;
        private Long subCategoryId;
        private List<LevelAnswerCommand> answers;

        public SubmitSubCategoryAnswers withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        @Getter
        @ToString
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LevelAnswerCommand{
            private String level;
            private Long optionId;
            private String textAnswer;
        }
    }

    @Getter
    @Builder
    @ToString
    public static class SubmitInteraction {
        @JsonIgnore
        private Long userId;

        private String interactionStyle;

        public SubmitInteraction withUserId(Long userId) {
            this.userId = userId;
            return this;
        }
    }


}