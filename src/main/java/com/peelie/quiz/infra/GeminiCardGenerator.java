package com.peelie.quiz.infra;

import com.peelie.quiz.domain.Quiz;
import com.peelie.quiz.domain.QuizGenerator;
import org.springframework.stereotype.Component;

@Component
public class GeminiCardGenerator implements QuizGenerator {

    @Override
    public Quiz generateQuiz() {
        return null;
    }
}
