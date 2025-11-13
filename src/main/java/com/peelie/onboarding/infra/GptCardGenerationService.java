package com.peelie.onboarding.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingInfo.StageCard;
import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingSubCategoryAnswers;
import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.Category;
import com.peelie.questionnaire.domain.category.CategoryReader;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import com.peelie.questionnaire.domain.question.QuestionOptionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptCardGenerationService {

        private final ObjectMapper objectMapper;
        private final OnboardingReaderImpl onboardingProcessReader;
        private final QuestionnaireService questionnaireService;
        private final CategoryReader categoryReader;
        private final SubCategoryReader subCategoryReader;

        @Value("${OPENAI_API_KEY:${openai.api.key:}}")
        private String openAiApiKey;

        // TODO: 모델명, temperature, 타임아웃 등을 설정값으로 분리
        private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
        private static final String GPT_MODEL = "gpt-3.5-turbo-1106";
        private static final double TEMPERATURE = 0.7;
        private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
        private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

        // TODO: 레벨 문자열을 상수로 분리하거나 QuestionLevel enum 활용
        private static final String LEVEL_L1 = "L1";
        private static final String LEVEL_L2 = "L2";
        private static final String LEVEL_L3 = "L3";
        private static final String LEVEL_L4 = "L4";

        @Transactional(readOnly = true)
        public CompletableFuture<OnboardingInfo.CardGeneration> generateCard(Long userId, List<Long> categoryIds) {
                try {
                        // ✅ 1. 유저의 온보딩 프로세스 조회
                        OnboardingProcess process = onboardingProcessReader.findOnboardingProcessByUserId(userId);

                        if (process == null) {
                                log.error("❌ Onboarding process not found for userId={}", userId);
                                OnboardingInfo.CardGeneration result = OnboardingInfo.CardGeneration.failed();
                                return CompletableFuture.completedFuture(result);
                        }

                        // ✅ 2. 카테고리 정보 및 질문 정보 수집
                        Map<Long, Category> categoryMap = new HashMap<>();
                        for (Long id : categoryIds) {
                                Category category = categoryReader.getCategory(id);
                                categoryMap.put(id, category);
                        }

                        // 서브카테고리 ID -> 카테고리 ID 매핑 및 서브카테고리 정보 수집
                        Map<Long, Long> subCategoryToCategoryMap = new HashMap<>();
                        Map<Long, SubCategory> subCategoryMap = new HashMap<>();
                        for (Category category : categoryMap.values()) {
                                for (SubCategory subCategory : category.getSubCategories()) {
                                        subCategoryToCategoryMap.put(subCategory.getId(), category.getId());
                                        subCategoryMap.put(subCategory.getId(), subCategory);
                                }
                        }

                        // 서브카테고리별 질문 정보 수집
                        Map<Long, Map<QuestionLevel, QuestionInfo>> questionsBySubCategory = new HashMap<>();
                        for (OnboardingSubCategoryAnswers answer : process.getSubCategoryAnswers()) {
                                Long subCategoryId = answer.getSubCategoryId();
                                if (!questionsBySubCategory.containsKey(subCategoryId)) {
                                        Long categoryId = subCategoryToCategoryMap.get(subCategoryId);
                                        if (categoryId != null) {
                                                List<QuestionInfo> questions =
                                                        questionnaireService.getQuestionsByIds(categoryId, subCategoryId);
                                                Map<QuestionLevel, QuestionInfo> questionMap = questions.stream()
                                                        .collect(Collectors.toMap(QuestionInfo::getLevel, q -> q));
                                                questionsBySubCategory.put(subCategoryId, questionMap);
                                        }
                                }
                        }

                        // ✅ 3. 사용자 답변 구성
                        List<Map<String, Object>> stage1Data = new ArrayList<>(); //1단 L0 + L1
                        List<Map<String, Object>> stage2Data = new ArrayList<>(); //2단 L2 + L3
                        List<Map<String, Object>> stage3Data = new ArrayList<>(); //3단 L4

                        for (OnboardingSubCategoryAnswers answer : process.getSubCategoryAnswers()) {
                                Long subCategoryId = answer.getSubCategoryId();
                                String level = answer.getLevel();
                                Map<QuestionLevel, QuestionInfo> questionMap = questionsBySubCategory.get(subCategoryId);

                                if (questionMap == null) continue; // DB에 없는 서브카테고리 방어 코드

                                Map<String, Object> answerDetail = new HashMap<>();
                                Long categoryId = subCategoryToCategoryMap.get(subCategoryId);
                                QuestionInfo questionInfo = questionMap.get(QuestionLevel.valueOf(level));

                                if (questionInfo != null) {
                                        answerDetail.put("subCategoryId", subCategoryId);
                                        answerDetail.put("level", level);
                                        answerDetail.put("question", questionInfo.getContent());

                                        if (LEVEL_L1.equals(level) || LEVEL_L2.equals(level) || LEVEL_L3.equals(level)) {
                                                Long optionId = answer.getOptionId();
                                                if (optionId != null) {
                                                        String optionContent = questionInfo.getOptions().stream()
                                                                .filter(opt -> opt.getOptionId().equals(optionId))
                                                                .map(QuestionOptionInfo::getContent)
                                                                .findFirst()
                                                                .orElse("선택한 옵션을 찾을 수 없습니다");
                                                        answerDetail.put("selectedOption", optionContent);
                                                }
                                        } else if (LEVEL_L4.equals(level)) {
                                                answerDetail.put("textAnswer", answer.getTextAnswer());
                                        }

                                        if (LEVEL_L1.equals(level)) {
                                                stage1Data.add(answerDetail);
                                        } else if (LEVEL_L2.equals(level) || LEVEL_L3.equals(level)) {
                                                stage2Data.add(answerDetail);
                                        } else if (LEVEL_L4.equals(level)) {
                                                stage3Data.add(answerDetail);
                                        }
                                }
                        }

                        // ✅ 4. 프롬프트 구성 (출력 JSON 형식 명시)
                        String prompt = """
                                                            당신은 사용자의 온보딩 설문 답변을 기반으로 3단계 카드를 생성하는 AI입니다.
                                                            각 단계별 카드는 {title, subtitle, content} 형식으로 생성해야 합니다.

                                                            ## 1단계 카드 (Stage 1)
                                                            - L0 질문(카테고리 질문)과 L1 답변을 기반으로 생성합니다.
                                                            - 카테고리명(categoryName)과 서브카테고리명(subCategoryName)을 반드시 실제 값으로 사용하세요.
                                                            - 카테고리 질문과 사용자가 선택한 L1 옵션 내용(selectedOption)을 반영하여 카드를 작성하세요.
                                                            - 절대 [카테고리명], [서브카테고리명] 같은 플레이스홀더를 사용하지 마세요. 반드시 실제 카테고리명과 서브카테고리명을 사용하세요.

                                                            ## 2단계 카드 (Stage 2)
                                                            - L2와 L3 답변을 기반으로 생성합니다.
                                                            - 사용자가 선택한 L2, L3 옵션 내용(selectedOption)을 반영하여 카드를 작성하세요.
                                                            - 절대 [L2 옵션], [L3 옵션] 같은 플레이스홀더를 사용하지 마세요. 반드시 실제 옵션 내용을 사용하세요.

                                                            ## 3단계 카드 (Stage 3)
                                                            - L4 답변(서술형 답변)을 기반으로 생성합니다.
                                                            - 사용자가 직접 입력한 텍스트 답변(textAnswer)을 반영하여 카드를 작성하세요.

                                                            ### 1단계 데이터 (L0 + L1):
                                                            %s

                                                            ### 2단계 데이터 (L2 + L3):
                                                            %s

                                                            ### 3단계 데이터 (L4):
                                                            %s

                                                            각 카드는 사용자의 답변을 자연스럽고 개인화된 방식으로 반영해야 합니다.
                                                            title은 간결하고 매력적이어야 하며, subtitle은 부제목 역할을 하고, content는 상세한 설명을 포함해야 합니다.
                                                            **중요: 데이터에 포함된 categoryName, subCategoryName, selectedOption, textAnswer 등의 실제 값을 그대로 사용하세요. 플레이스홀더를 사용하지 마세요.**

                                                            반드시 다음 JSON 형식으로만 응답하세요:
                                        {
                                          "stage1": {"title": "...", "subtitle": "...", "content": "..."},
                                          "stage2": {"title": "...", "subtitle": "...", "content": "..."},
                                          "stage3": {"title": "...", "subtitle": "...", "content": "..."}
                                        }
                                                            """
                                .formatted(
                                        objectMapper.writeValueAsString(stage1Data),
                                        objectMapper.writeValueAsString(stage2Data),
                                        objectMapper.writeValueAsString(stage3Data));


                        // ✅ 5. OpenAI 요청 JSON 생성
                        String requestJson = """
                {
                  "model": "%s",
                  "messages": [
                    {"role": "system", "content": "You are a helpful assistant that returns card information in a valid JSON format."},
                    {"role": "user", "content": %s}
                  ],
                  "temperature": %s,
                  "response_format": {"type": "json_object"}
                }
                """.formatted(GPT_MODEL, objectMapper.writeValueAsString(prompt), TEMPERATURE);

                        // ✅ 6. HTTP 요청 전송
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setBearerAuth(openAiApiKey);

                        RestTemplate restTemplate = new RestTemplateBuilder()
                                .connectTimeout(CONNECT_TIMEOUT)
                                .readTimeout(READ_TIMEOUT)
                                .build();

                        ResponseEntity<String> response = restTemplate.exchange(
                                OPENAI_URL,
                                HttpMethod.POST,
                                new HttpEntity<>(requestJson, headers),
                                String.class
                        );

                        log.info("📡 GPT 응답 코드: {}", response.getStatusCode());

                        // ✅ 7. 응답 파싱
                        String raw = response.getBody();
                        JsonNode root = objectMapper.readTree(raw);
                        String content = root.path("choices").get(0).path("message").path("content").asText();
                        String cleaned = content == null ? "" : content.trim();
                        if (cleaned.startsWith("```")) {
                                int firstBrace = cleaned.indexOf('{');
                                int lastBrace = cleaned.lastIndexOf('}');
                                if (firstBrace >= 0 && lastBrace > firstBrace) {
                                        cleaned = cleaned.substring(firstBrace, lastBrace + 1);
                                }
                        }
                        if (cleaned.isEmpty() || "{}".equals(cleaned)) {
                                log.warn("⚠️ GPT content is empty or {}. Raw: {}", content, raw);
                        }
                        JsonNode cardJson = objectMapper.readTree(cleaned.isEmpty() ? "{}" : cleaned);

                        StageCard s1 = StageCard.builder()
                                .title(cardJson.path("stage1").path("title").asText(""))
                                .subtitle(cardJson.path("stage1").path("subtitle").asText(""))
                                .content(cardJson.path("stage1").path("content").asText(""))
                                .build();

                        StageCard s2 = StageCard.builder()
                                .title(cardJson.path("stage2").path("title").asText(""))
                                .subtitle(cardJson.path("stage2").path("subtitle").asText(""))
                                .content(cardJson.path("stage2").path("content").asText(""))
                                .build();

                        StageCard s3 = StageCard.builder()
                                .title(cardJson.path("stage3").path("title").asText(""))
                                .subtitle(cardJson.path("stage3").path("subtitle").asText(""))
                                .content(cardJson.path("stage3").path("content").asText(""))
                                .build();
                        OnboardingInfo.CardGeneration result = OnboardingInfo.CardGeneration.done(s1, s2, s3);

                        // [핵심 수정]: 최종 결과를 CompletableFuture로 감싸서 반환
                        return CompletableFuture.completedFuture(result);

                } catch (Exception e) {
                        log.error("❌ GPT API 호출 실패", e);
                        OnboardingInfo.CardGeneration result = OnboardingInfo.CardGeneration.failed();
                        return CompletableFuture.completedFuture(result);
                }
        }
}
