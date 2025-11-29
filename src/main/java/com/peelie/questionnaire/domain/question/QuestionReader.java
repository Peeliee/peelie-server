package com.peelie.questionnaire.domain.question;

public interface QuestionReader {
    Question getQuestionById(Long questionId);
    QuestionOption getQuestionOptionById(Long optionId);
}

