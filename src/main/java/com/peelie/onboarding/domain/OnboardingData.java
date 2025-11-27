package com.peelie.onboarding.domain;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingData {
    //RequestDTO for gpt api
    private List<CategoryAnswer> stage1;
    private List<CategoryAnswer> stage2;
    private List<CategoryAnswer> stage3;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryAnswer {
        private String userName;
        private String categoryName;
        private List<Answer> answers;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Answer {
            private String level;
            private String question;
            private String answer;
        }
    }
}