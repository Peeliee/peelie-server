package com.peelie.quiz.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GeminiQuizResponse {

    //생성된 퀴즈 목록
    @JsonProperty("quizzes")
    private List<GeminiQuizData> quizzes;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiQuizData {

        // 퀴즈 질문
        @JsonProperty("question")
        private String question;

        // 정답
        @JsonProperty("rightAnswer")
        private String rightAnswer;

        // 오답
        @JsonProperty("wrongAnswer")
        private String wrongAnswer;

        // stage (1, 2, 3)
        // String으로 받아서 파싱
        @JsonProperty("stage")
        private String stage;
    }
}

