package com.peelie.prompt;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PromptCommand {
    private final Long userId;
    private final String userName;
    private final List<SubCategoryAnswer> answers;

    @Getter
    @Builder
    public static class SubCategoryAnswer {
        private final String categoryName;
        private final String subCategoryName;
        private final String categoryQuestion; // L0 - Category의 categoryQuestion
        private final List<QuestionAnswer> questionAnswers;
    }

    @Getter
    @Builder
    public static class QuestionAnswer {
        private final String level; // L1, L2, L3, L4
        private final String question;
        private final String answer; // 선택지 내용 또는 텍스트 답변
    }
}

