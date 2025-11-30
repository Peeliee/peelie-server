package com.peelie.onboarding.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        public static class LevelAnswerCommand {
            private String level;

            @JsonAlias({ "L1AnswerId", "L2AnswerId", "L3AnswerId" })
            private Long optionId;

            @JsonProperty("L4Answer")
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
