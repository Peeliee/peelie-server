package com.peelie.quiz.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizCommand {

    private QuizStage quizStage;
    private String question;
    private String rightAnswer;
    private String wrongAnswer;

    public Quiz toEntity(Long userId) {
        return Quiz.builder()
                .userId(userId)
                .stage(quizStage)
                .question(question)
                .rightAnswer(rightAnswer)
                .wrongAnswer(wrongAnswer)
                .build();
    }
}
