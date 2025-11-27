package com.peelie.quiz.domain;

import com.peelie.prompt.PromptCommand;
import com.peelie.prompt.PromptGenerator;
import com.peelie.prompt.UserAnswerLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final UserAnswerLoader userAnswerLoader;
    private final PromptGenerator promptGenerator;
    private final QuizGenerator quizGenerator;

    @Override
    public List<QuizInfo> createQuiz(Long userId) {
        PromptCommand promptCommand = userAnswerLoader.generatePromptCommand(userId);
        String prompt = promptGenerator.generatePrompt(promptCommand);
        return quizGenerator.generateQuiz(prompt);
    }
}
