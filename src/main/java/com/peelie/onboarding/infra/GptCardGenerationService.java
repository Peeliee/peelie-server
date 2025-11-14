package com.peelie.onboarding.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingInfo.StageCard;
import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingSubCategoryAnswers;
import com.peelie.profile.domain.Profile;
import com.peelie.profile.domain.ProfileReader;
import com.peelie.profile.domain.ProfileService;
import com.peelie.profile.domain.ProfileStore;
import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.Category;
import com.peelie.questionnaire.domain.category.CategoryReader;
import com.peelie.questionnaire.domain.category.SubCategory;
import com.peelie.questionnaire.domain.category.SubCategoryReader;
import com.peelie.questionnaire.domain.question.QuestionInfo;
import com.peelie.questionnaire.domain.question.QuestionLevel;
import com.peelie.questionnaire.domain.question.QuestionOptionInfo;
import com.theokanning.openai.service.FunctionExecutor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import com.theokanning.openai.completion.chat.ChatFunction; // [추가]
import com.theokanning.openai.completion.chat.*; // [추가]
import com.theokanning.openai.service.OpenAiService; // [추가]


@Slf4j
@Component
@RequiredArgsConstructor
public class GptCardGenerationService {

    private final ObjectMapper objectMapper;
    private final OnboardingReaderImpl onboardingProcessReader;
    private final QuestionnaireService questionnaireService;
    private final CategoryReader categoryReader;
    private final SubCategoryReader subCategoryReader;
    private final ProfileReader profileReader;

    private final ProfileStore profileStore; // db 저장 위해 생성자 주입
    @Value("${OPENAI_API_KEY:${openai.api.key:}}")
    private String openAiApiKey;

    // TODO: 모델명, temperature, 타임아웃 등을 설정값으로 분리
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String GPT_MODEL = "gpt-3.5-turbo-1106";
    private static final double TEMPERATURE = 0.7;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

    @Getter
    @NoArgsConstructor
    public static class StageCardPayload {
        @JsonPropertyDescription("카드의 메인 제목 (간결하고 매력적으로)")
        @JsonProperty(required = true)
        private String title;

        @JsonPropertyDescription("카드의 부제목 (요약)")
        @JsonProperty(required = true)
        private String subtitle;

        @JsonPropertyDescription("카드의 상세 설명 내용")
        @JsonProperty(required = true)
        private String content;
    }

    @Transactional
    public CompletableFuture<OnboardingInfo.CardGeneration> generateCard(Long userId, List<Long> categoryIds) {
        try {
            //  1. 유저의 온보딩 프로세스 조회
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

            //  3. 사용자 답변 정보 Question DB에서 fetching
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

                    if (QuestionLevel.L1.name().equals(level) || QuestionLevel.L2.name().equals(level) || QuestionLevel.L3.name().equals(level)) {
                        Long optionId = answer.getOptionId();
                        if (optionId != null) {
                            String optionContent = questionInfo.getOptions().stream()
                                    .filter(opt -> opt.getOptionId().equals(optionId))
                                    .map(QuestionOptionInfo::getContent)
                                    .findFirst()
                                    .orElse("선택한 옵션을 찾을 수 없습니다");
                            answerDetail.put("selectedOption", optionContent);
                        }

                    } else if (QuestionLevel.L4.name().equals(level)) {
                        answerDetail.put("textAnswer", answer.getTextAnswer());
                    }

                    if (QuestionLevel.L1.name().equals(level)) {
                        stage1Data.add(answerDetail);
                    } else if (QuestionLevel.L2.name().equals(level) || QuestionLevel.L3.name().equals(level)) {
                        stage2Data.add(answerDetail);
                    } else if (QuestionLevel.L4.name().equals(level)) {
                        stage3Data.add(answerDetail);
                    }

                }
            }

            //  4. 프롬프트 구성
            String prompt = """
                    당신은 사용자의 온보딩 설문 답변을 기반으로 3단계 카드를 생성하는 AI입니다.
                    제공된 1, 2, 3단계 데이터를 기반으로 각 카드를 생성해주세요.

                    **반드시 아래 형식의 JSON으로 응답하세요:**
                    {
                      "stage1": {"title": "...", "subtitle": "...", "content": "..."},
                      "stage2": {"title": "...", "subtitle": "...", "content": "..."},
                      "stage3": {"title": "...", "subtitle": "...", "content": "..."}
                    }

                    ### 1단계 데이터 (L0 + L1):
                    %s

                    ### 2단계 데이터 (L2 + L3):
                    %s

                    ### 3단계 데이터 (L4):
                    %s
                    """.formatted(
                    objectMapper.writeValueAsString(stage1Data),
                    objectMapper.writeValueAsString(stage2Data),
                    objectMapper.writeValueAsString(stage3Data)
            );

            //  5. OpenAI request용 JSON 생성
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

            // 6. HTTP 요청 전송 - RestTemplate 기반인데 재생성때  OpenAiService openAiService 사용 예정
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

            //  7. 응답 파싱
            String raw = response.getBody();
            JsonNode root = objectMapper.readTree(raw);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            JsonNode cardJson = objectMapper.readTree(content);
            log.info(" GPT 원본 응답: {}", content);
            log.info(" cardJson: {}", cardJson);
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
            // 카드 정보를 JSON으로 변환
            Map<String, Object> cardData = Map.of(
                    "stage1", Map.of("title", s1.getTitle(), "subtitle", s1.getSubtitle(), "content", s1.getContent()),
                    "stage2", Map.of("title", s2.getTitle(), "subtitle", s2.getSubtitle(), "content", s2.getContent()),
                    "stage3", Map.of("title", s3.getTitle(), "subtitle", s3.getSubtitle(), "content", s3.getContent())
            );

            String cardInfo = objectMapper.writeValueAsString(cardData);


            Profile profile = profileReader.getProfileByUserId(userId);
            profile.updateCardInfoJson(cardInfo);
            profileStore.store(profile);

            // [핵심 수정]: 최종 결과를 CompletableFuture로 감싸서 반환
            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            log.error("❌ GPT API 호출 실패", e);
            OnboardingInfo.CardGeneration result = OnboardingInfo.CardGeneration.failed();
            return CompletableFuture.completedFuture(result);
        }
    }


    // GptCardGenerationService.java
    @Transactional
    public OnboardingInfo.CardGeneration regenerateStageCard(Long userId, Integer stage) {

        try {
            // ✅ 1. cardInfoJson 조회
            Profile profile = profileReader.getProfileByUserId(userId);
            String cardInfoJson = profile.getCardInfoJson();

            if (cardInfoJson == null || cardInfoJson.isEmpty()) {
                throw new IllegalStateException("기존 카드 정보가 없습니다.");
            }

            Map<String, Object> cardData = objectMapper.readValue(
                    cardInfoJson,
                    new TypeReference<Map<String, Object>>() {
                    }
            );

            String stageKey = "stage" + stage;
            Map<String, Object> stageMap = (Map<String, Object>) cardData.get(stageKey);

            if (stageMap == null) {
                throw new IllegalArgumentException("해당 단계의 카드가 없습니다.");
            }

            String title = (String) stageMap.get("title");
            String subtitle = (String) stageMap.get("subtitle");
            String content = (String) stageMap.get("content");

            // ✅ 2. 프롬프트 구성
            String prompt = """
                    당신은 카드 내용을 개선하는 AI입니다.
                    ...
                    """.formatted(title, subtitle, content);

            // ✅ 3. GPT 호출
            JsonNode improved = callGptApi(prompt);

            // ✅ 4. StageCard 생성
            StageCard improvedCard = StageCard.builder()
                    .title(improved.path("title").asText(""))
                    .subtitle(improved.path("subtitle").asText(""))
                    .content(improved.path("content").asText(""))
                    .build();

            // ✅ 5. cardInfoJson 업데이트
            cardData.put(stageKey, Map.of(
                    "title", improvedCard.getTitle(),
                    "subtitle", improvedCard.getSubtitle(),
                    "content", improvedCard.getContent()
            ));

            String updatedJson = objectMapper.writeValueAsString(cardData);
            profile.updateCardInfoJson(updatedJson);

            // ✅ 6. 전체 카드 정보 반환
            return buildCardGenerationFromMap(cardData);

        } catch (Exception e) {
            log.error("❌ 카드 재생성 실패 - userId={}, stage={}", userId, stage, e);
            return OnboardingInfo.CardGeneration.failed();
        }
    }

    // GPT 호출 로직을 별도 private 메서드로 분리
    private JsonNode callGptApi(String prompt) throws Exception {
        String requestJson = """
                {
                  "model": "%s",
                  "messages": [
                    {"role": "system", "content": "You are a helpful assistant that returns valid JSON."},
                    {"role": "user", "content": %s}
                  ],
                  "temperature": %s,
                  "response_format": {"type": "json_object"}
                }
                """.formatted(GPT_MODEL, objectMapper.writeValueAsString(prompt), TEMPERATURE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        RestTemplate restTemplate = new RestTemplateBuilder().connectTimeout(CONNECT_TIMEOUT).readTimeout(READ_TIMEOUT)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                new HttpEntity<>(requestJson, headers),
                String.class
        );

        String raw = response.getBody();
        JsonNode root = objectMapper.readTree(raw);
        String contentStr = root.path("choices").get(0).path("message").path("content").asText();
        return objectMapper.readTree(contentStr);
    }

    // Map에서 CardGeneration 객체를 생성하는 유틸리티 메서드
    private OnboardingInfo.CardGeneration buildCardGenerationFromMap(Map<String, Object> cardData) {
        return OnboardingInfo.CardGeneration.done(
                toStageCard((Map<String, Object>) cardData.get("stage1")),
                toStageCard((Map<String, Object>) cardData.get("stage2")),
                toStageCard((Map<String, Object>) cardData.get("stage3"))
        );
    }

    // Map에서 StageCard 객체를 생성하는 유틸리티 메서드
    private StageCard toStageCard(Map<String, Object> map) {
        if (map == null) return StageCard.builder().build();
        return StageCard.builder()
                .title((String) map.get("title"))
                .subtitle((String) map.get("subtitle"))
                .content((String) map.get("content"))
                .build();
    }
}