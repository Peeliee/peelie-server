package com.peelie.questionnaire.domain.question;

import lombok.Getter;

@Getter
public class QuestionOptionInfo {
    private final Long optionId;
    private final String content;

    public QuestionOptionInfo(QuestionOption option) {
        this.optionId = option.getId();
        this.content = option.getContent();
    }
}
