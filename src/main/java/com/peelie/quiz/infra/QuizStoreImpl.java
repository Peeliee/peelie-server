package com.peelie.quiz.infra;

import com.peelie.quiz.domain.Quiz;
import com.peelie.quiz.domain.QuizStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizStoreImpl implements QuizStore {

    private final QuizRepository quizRepository;

    @Override
    public Quiz store(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public List<Quiz> storeAll(List<Quiz> quizzes) {
        return quizRepository.saveAll(quizzes);
    }
}
