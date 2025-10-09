package com.peelie.questionnaire.domain.question;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestionType {
    CHOICE("선택형"),
    TEXT("서술형");

    private final String description;
}
