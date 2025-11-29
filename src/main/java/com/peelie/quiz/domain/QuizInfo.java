package com.peelie.quiz.domain;

import lombok.Getter;

@Getter
public class QuizInfo {

    private Long id;
    private Long userId;
    private QuizStage stage; // 1단계, 2단계, 3단계
    private String question;
    private String rightAnswer;
    private String wrongAnswer;

    public QuizInfo(Quiz quiz) {
        this.id = quiz.getId();
        this.userId = quiz.getUserId();
        this.stage = quiz.getStage();
        this.question = quiz.getQuestion();
        this.rightAnswer = quiz.getRightAnswer();
        this.wrongAnswer =  quiz.getWrongAnswer();
    }
}
