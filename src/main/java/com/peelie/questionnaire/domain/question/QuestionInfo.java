package com.peelie.questionnaire.domain.question;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class QuestionInfo {
    private final Long questionId;
    private final QuestionLevel level;
    private final QuestionType type;
    private final String content;
    private final List<QuestionOptionInfo> options;

    public QuestionInfo(Question question) {
        this.questionId = question.getId();
        this.level = question.getLevel();
        this.type = question.getType();
        this.content = question.getContent();
        this.options = question.getOptions().stream()
                .map(QuestionOptionInfo::new)
                .collect(Collectors.toList());
    }
}
