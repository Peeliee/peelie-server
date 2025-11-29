package com.peelie.quiz.interfaces;

import com.peelie.quiz.domain.QuizInfo;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class QuizDto {

    @Getter
    @ToString
    public static class QuizResponse {
        private Long quizId;
        private String quiz;
        private Integer answerNo = 1;
        private List<Answer> answer = new ArrayList<>() {
            Answer answer1 = new Answer(1);
            Answer answer2 = new Answer(2);
        };

        public QuizResponse(QuizInfo quizInfo) {
            this.quizId = quizInfo.getId();
            this.quiz = quizInfo.getQuestion();
            this.answer.get(0).text = quizInfo.getRightAnswer();
            this.answer.get(1).text = quizInfo.getWrongAnswer();
        }
    }

    public static class Answer{
        private Integer optionNo;
        private String text;

        public Answer(Integer optionNo) {
            this.optionNo = optionNo;
        }
    }
}
