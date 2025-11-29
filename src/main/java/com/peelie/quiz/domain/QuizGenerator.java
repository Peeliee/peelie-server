package com.peelie.quiz.domain;

import java.util.List;

public interface QuizGenerator {
    List<QuizCommand> generateQuiz(String prompt);
}
