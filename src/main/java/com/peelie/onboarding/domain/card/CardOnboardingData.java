package com.peelie.onboarding.domain.card;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardOnboardingData {
    //RequestDTO for gpt api
    private List<CategoryAnswer> stage1;
    private List<CategoryAnswer> stage2;
    private List<CategoryAnswer> stage3;

    @Getter
    @Builder
    public static class CategoryAnswer {
        private String userName;
        private String categoryName;
        private String categoryQuestion;
        private List<Answer> answers;


    }
    @Getter
    @Builder
    public static class Answer {
        private String level;
        private String question;
        private String answer;
    }
}