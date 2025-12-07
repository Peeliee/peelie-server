package com.peelie.quiz.domain;

import com.peelie.prompt.PromptGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final PromptGenerator promptGenerator;
    private final QuizGenerator quizGenerator;
    private final QuizStore quizStore;

    @Override
    public List<QuizCommand> createQuiz(Long userId) {
        String prompt = promptGenerator.generatePrompt(userId);
        return quizGenerator.generateQuiz(prompt);
    }

    @Override
    @Transactional
    public void registerQuiz(Long userId, List<QuizCommand> commands) {
        List<Quiz> quizzes = commands.stream()
                .map(command -> command.toEntity(userId))
                .toList();
        quizStore.storeAll(quizzes);
    }
}
