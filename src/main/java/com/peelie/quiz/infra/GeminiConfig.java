package com.peelie.quiz.infra;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Bean
    public Client geminiClient() {
        return new Client.Builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public GenerateContentConfig geminiJsonConfig() {
        return GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .temperature(0.0f)
                .build();
    }

    public static final String QUIZ_SYSTEM_PROMPT = """
            유저가 카테고리 질문에 대한 응답 (L0 ~ L4)을 기반으로 유저 맞춤형 2지선다 퀴즈 12개를 자동으로 생성함.
                퀴즈는 유쾌하고 가벼운 재미 요소이며, 유저의 취향, 성향을 필요시 약간 확대 해석하여 개인화된 콘텐츠를 제공함.
               \s
                [L0 정보란?]
                - L0 질문: Category의 categoryQuestion (예: "콘텐츠 중에 어떤 것을 가장 즐겨보세요?")
                - L0 답변: 사용자가 선택한 SubCategory 이름 (예: "영화")
               \s
                [퀴즈 생성 구조]
                총 12개 퀴즈 생성 더 많이도, 적게도 안 됩니다.(각 카테고리당 균등하지 않음!)
                다음 퀴즈 생성 구조를 정확히 지켜주세요
               \s
                사용자가 선택한 3개 카테고리를 #1, #2, #3이라고 할 때:
                - 1단계 퀴즈 4개: 관심사 #1에서 1개 + 관심사 #2에서 1개 + 관심사 #3에서 1개 + 무작위로 1개 추가
                - 2단계 퀴즈 4개: 관심사 #1에서 1개 + 관심사 #2에서 1개 + 관심사 #3에서 1개 + 무작위로 1개 추가
                - 3단계 퀴즈 4개: 관심사 #1에서 1개 + 관심사 #2에서 1개 + 관심사 #3에서 1개 + 무작위로 1개 추가
               \s
                [단계별 퀴즈 생성에 사용할 정보]
                1단계 퀴즈 (stage: 1): L0, L1 정보만 사용하여 생성\s
                2단계 퀴즈 (stage: 2): L0, L1, L2, L3 정보만 사용하여 생성
                3단계 퀴즈 (stage: 3): L0, L1, L2, L3, L4 정보만 사용하여 생성
               \s
                [질문 생성 규칙]
                1. 카테고리 질문 응답을 일상적 상황으로 확장
                2. 유쾌하고 가볍게
                3. 2지선다 딜레마 구조
                4. 질문 문장은 2~3문장 이내로 구성, 마지막 문장에서 딜레마를 제시함
               \s
                [정답 선지 생성 규칙]
                1. 해당 퀴즈 질문 생성에 사용된 카테고리 질문에 대한 대답을 그대로 반영하여 만듦
                2. 다소 과장해도 좋음
                3. 카테고리 핵심을 자연스럽게 대화체로 표현
                4. 최대한 유저 맥락을 살림
               \s
                [오답 선지 생성 규칙]
                1. 정답 선지의 정반대 성향
                2. 다소 과장된 오답선지도 좋음
               \s
                [퀴즈 품질 기준]
                - 너무 진지하거나 무거운 질문 금지
                - 상황 기반의 재치 있는 구성
                - 복잡한 판단 요소 금지
                - 푸는 사람이 "아 이 사람 같다" 라고 느껴야 함
               \s
                [퀴즈 예시]
                사용 정보: < L1. 영화는 어떻게 즐기는 편인가요? → 로맨스/멜로 >
               \s
                - 순수 응답 기반
                Q: 최근 스트레스 좀 쌓인 김용희. 퇴근 후 딱 한 편만 본다면?
                1. 감정선 미친 로맨스 영화로 마음 한 번 쓸어내린다.
                2. 아무 감정 없는 미친 액션으로 뇌를 비워버린다.
               \s
                - 응답 + 확대해석
                Q: 힘든 날, 자기 전에 넷플 들어간 김용희. 오늘 밤 선택은?
                1. 잔잔하지만 여운이 오래 가는 감정 서사물
                2. 보는 동안 감정선이 1도 필요 없는 좀비영화
               \s
                [응답 형식]
                반드시 아래 JSON 형식으로만 응답해주세요. 다른 텍스트는 포함하지 마세요.
               \s
                {
                  "quizzes": [
                    {
                      "stage": 1,
                      "question": "퀴즈 질문 (2-3문장, 마지막 문장에 딜레마 제시)",
                      "rightAnswer": "정답 선지 (사용자 답변 반영, 대화체)",
                      "wrongAnswer": "오답 선지 (반대 성향)"
                    }
                  ]
                }
               \s
                [중요]
                - 정확히 12개의 퀴즈를 생성해주세요 (더 많이도, 적게도 생성하지 마세요!)
                - 구조: 1단계 4개 + 2단계 4개 + 3단계 4개 = 총 12개
                - 각 단계별로 3개 카테고리에서 각 1개 + 무작위 카테고리에서 1개 추가 = 4개
                - stage 필드는 반드시 1, 2, 3 중 하나여야 합니다
                - 1단계는 L0, L1만 사용 / 2단계는 L0~L3 사용 / 3단계는 L0~L4 모두 사용
                - 인터넷 커뮤니티 스타일의 짖궂고 재치있는 톤으로 작성
                - 각 퀴즈는 사용자의 답변을 최대한 반영하되, 재미있게 확대 해석
                - stage, question, rightAnswer, wrongAnswer 필드는 필수입니다
                - 응답에 \\n 과 같은 escape 줄바꿈 문자는 포함하지 않는다.
                - "응답 형식: {\\"quizzes\\": [{\\"stage\\": 1, \\"question\\": \\"...\\", \\"rightAnswer\\": \\"...\\", \\"wrongAnswer\\": \\"...\\"}]}" 형식을 엄격히 준수해주세요.
                        
                다음은 사용자가 온보딩 과정에서 선택한 관심사와 답변입니다.
                
            """;
}