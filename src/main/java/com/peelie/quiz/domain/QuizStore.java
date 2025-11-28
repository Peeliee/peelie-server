package com.peelie.quiz.domain;

import java.util.List;

public interface QuizStore {
    Quiz store(Quiz quiz);
    List<Quiz> storeAll(List<Quiz> quizzes);
}
