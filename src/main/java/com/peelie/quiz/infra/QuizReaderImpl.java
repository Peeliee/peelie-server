package com.peelie.quiz.infra;

import com.peelie.quiz.domain.Quiz;
import com.peelie.quiz.domain.QuizReader;
import com.peelie.quiz.exception.QuizNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizReaderImpl implements QuizReader {

    private final QuizRepository quizRepository;

    @Override
    public Quiz getQuiz(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(QuizNotFoundException::new);
    }

    @Override
    public Quiz getQuizByUserId(Long userId) {
        return quizRepository.findByUserId(userId)
                .orElseThrow(() -> new QuizNotFoundException("userId: " + userId + ", 해당 유저의 퀴즈가 존재하지 않습니다."));
    }
}
