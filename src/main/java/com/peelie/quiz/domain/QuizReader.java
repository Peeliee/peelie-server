package com.peelie.quiz.domain;

public interface QuizReader {
    Quiz getQuiz(Long id);
    Quiz getQuizByUserId(Long userId);
}
