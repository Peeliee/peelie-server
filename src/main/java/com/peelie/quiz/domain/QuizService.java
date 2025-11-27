package com.peelie.quiz.domain;

import java.util.List;

public interface QuizService {
    List<QuizInfo> createQuiz(Long userId);
}
