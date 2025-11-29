package com.peelie.quiz.domain;

import java.util.List;

public interface QuizService {
    List<QuizCommand> createQuiz(Long userId);
    void registerQuiz(Long userId, List<QuizCommand> quizzes);
}
