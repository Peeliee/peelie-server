package com.peelie.quiz.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.quiz.domain.Quiz;
import com.peelie.quiz.domain.QuizGenerator;
import com.peelie.quiz.domain.QuizInfo;
import com.peelie.quiz.domain.QuizStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import static com.peelie.quiz.infra.GeminiConfig.QUIZ_SYSTEM_PROMPT;

@Component
@RequiredArgsConstructor
public class GeminiQuizGenerator implements QuizGenerator {

    private final Client geminiClient;
    private final GenerateContentConfig geminiJsonConfig;
    private final ObjectMapper objectMapper;

    @Override
    public List<QuizInfo> generateQuiz(String prompt) {
        try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    "gemini-2.0-flash",
                    QUIZ_SYSTEM_PROMPT + prompt,
                    geminiJsonConfig
            );

            String responseText = response.text();

            // JSON 파싱 및 QuizInfo 리스트 생성
            List<QuizInfo> allQuizzes = parseGeminiResponse(responseText);

            return allQuizzes;

        } catch (Exception e) {
            throw new BaseException("퀴즈 생성에 실패했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private List<QuizInfo> parseGeminiResponse(String responseJson) {
        try {
            // JSON을 GeminiQuizResponse로 역직렬화
            GeminiQuizResponse response = objectMapper.readValue(responseJson, GeminiQuizResponse.class);

            if (response.getQuizzes() == null || response.getQuizzes().isEmpty()) {
                throw new BaseException("퀴즈 데이터가 비어있습니다", ErrorCode.INTERNAL_SERVER_ERROR);
            }

            // 각 퀴즈 데이터를 QuizInfo로 변환
            List<QuizInfo> quizInfoList = new ArrayList<>();
            for (GeminiQuizResponse.GeminiQuizData quizData : response.getQuizzes()) {
                QuizInfo quizInfo = convertToQuizInfo(quizData);
                if (quizInfo != null) {
                    quizInfoList.add(quizInfo);
                }
            }

            return quizInfoList;

        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException("퀴즈 응답 파싱에 실패했습니다: " + e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // GeminiQuizData를 QuizInfo로 변환
    private QuizInfo convertToQuizInfo(GeminiQuizResponse.GeminiQuizData quizData) {
        // 필수 필드 검증
        if (!isValidQuizData(quizData)) {
            return null;
        }

        // stage 값을 Integer로 파싱
        int stageValue = parseStageValue(quizData.getStage());
        if (stageValue == -1) {
            return null;
        }

        // stage Integer를 QuizStage enum으로 변환
        QuizStage stage = convertToQuizStage(stageValue);
        if (stage == null) {
            return null;
        }

        // Quiz 엔티티 생성
        // TODO: 퀴즈 엔티티 말고 별도의 클래스 만들어서 하는 걸로 개선하기
        Quiz quiz = Quiz.builder()
                .userId(null)
                .stage(stage)
                .question(quizData.getQuestion())
                .rightAnswer(quizData.getRightAnswer())
                .wrongAnswer(quizData.getWrongAnswer())
                .build();

        return new QuizInfo(quiz);
    }

    // 퀴즈 데이터 필드가 모두 존재하는지 검증
    private boolean isValidQuizData(GeminiQuizResponse.GeminiQuizData quizData) {
        return quizData.getQuestion() != null && !quizData.getQuestion().isBlank()
                && quizData.getRightAnswer() != null && !quizData.getRightAnswer().isBlank()
                && quizData.getWrongAnswer() != null && !quizData.getWrongAnswer().isBlank()
                && quizData.getStage() != null && !quizData.getStage().isBlank();
    }

    private int parseStageValue(String stageStr) {
        return Integer.parseInt(stageStr);
    }

    private QuizStage convertToQuizStage(int stageValue) {
        return QuizStage.fromValue(stageValue);
    }
}
