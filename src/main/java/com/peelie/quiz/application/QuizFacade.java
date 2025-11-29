package com.peelie.quiz.application;

import com.peelie.quiz.domain.QuizCommand;
import com.peelie.quiz.domain.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizFacade {

    private final QuizService quizService;

    public void createUserQuiz(Long userId) {
        List<QuizCommand> initQuizzes = quizService.createQuiz(userId);
        quizService.registerQuiz(userId, initQuizzes);
    }
}
