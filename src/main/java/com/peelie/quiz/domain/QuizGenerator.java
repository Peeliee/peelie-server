package com.peelie.quiz.domain;

import java.util.List;

public interface QuizGenerator {
    List<QuizInfo> generateQuiz(String prompt);
}
