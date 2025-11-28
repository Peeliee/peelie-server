package com.peelie.quiz.interfaces;

import com.peelie.common.context.UserContextHolder;
import com.peelie.common.response.SuccessResponse;
import com.peelie.quiz.application.QuizFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController implements QuizDoc {

    private final QuizFacade quizFacade;

    @PostMapping()
    public SuccessResponse<String> createUserQuiz() {
        Long userId = UserContextHolder.getUserId();
        quizFacade.createUserQuiz(userId);
        return SuccessResponse.created("Quiz Created Successfully");
    }
}
