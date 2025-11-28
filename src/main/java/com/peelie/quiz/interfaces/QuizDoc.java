package com.peelie.quiz.interfaces;

import com.peelie.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Quiz", description = "퀴즈 API 명세")
public interface QuizDoc {

    @Operation(summary = "퀴즈 생성", description = "현재 로그인한 사용자온보딩 답변 바탕으로 퀴즈 생성 후 DB에 저장")
    SuccessResponse createUserQuiz();
}
