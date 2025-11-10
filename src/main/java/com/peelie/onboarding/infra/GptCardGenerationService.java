package com.peelie.onboarding.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingInfo.StageCard;
import com.peelie.onboarding.domain.OnboardingProcess;
import com.peelie.onboarding.domain.OnboardingSubCategoryAnswers;
import com.peelie.questionnaire.domain.QuestionnaireService;
import com.peelie.questionnaire.domain.category.Category;
import com.peelie.questionnaire.domain.category.CategoryId;
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
        private static final String GPT_MODEL = "gpt-3.5-turbo";
        private static final double TEMPERATURE = 0.7;
        private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
        private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

        // TODO: 레벨 문자열을 상수로 분리하거나 QuestionLevel enum 활용
        private static final String LEVEL_L1 = "L1";
        private static final String LEVEL_L2 = "L2";
        private static final String LEVEL_L3 = "L3";
        private static final String LEVEL_L4 = "L4";

        @Transactional(readOnly = true)
        public OnboardingInfo.CardGeneration generateCard(Long userId, List<Long> categoryIds) {
                try {
                        // ✅ 1. 유저의 온보딩 프로세스 조회

                        OnboardingProcess process = onboardingProcessReader.findOnboardingProcessByUserId(userId);

                        if (process == null) {
                                log.error("❌ Onboarding process not found for userId={}", userId);
                                return OnboardingInfo.CardGeneration.failed();
                        }

                        // ✅ 2. 카테고리 정보 및 질문 정보 수집
                        Map<Long, Category> categoryMap = categoryIds.stream()
                                        .collect(Collectors.toMap(
                                                        id -> id,
                                                        categoryReader::getCategory));

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
                                                List<QuestionInfo> questions = questionnaireService
                                                                .getQuestionsByIds(categoryId, subCategoryId);
                                                Map<QuestionLevel, QuestionInfo> questionMap = questions.stream()
                                                                .collect(Collectors.toMap(
                                                                                QuestionInfo::getLevel,
                                                                                q -> q));
                                                questionsBySubCategory.put(subCategoryId, questionMap);
                                        }
                                }
                        }

                        // ✅ 3. 사용자 답변을 상세 정보와 함께 구성
                        // TODO: Map<String, Object> 대신 전용 DTO 클래스 생성 (AnswerDetailDto 등)
                        List<Map<String, Object>> stage1Data = new ArrayList<>(); // L0 + L1
                        List<Map<String, Object>> stage2Data = new ArrayList<>(); // L2 + L3
                        List<Map<String, Object>> stage3Data = new ArrayList<>(); // L4

                        for (OnboardingSubCategoryAnswers answer : process.getSubCategoryAnswers()) {
                                Long subCategoryId = answer.getSubCategoryId();
                                String level = answer.getLevel();
                                Map<QuestionLevel, QuestionInfo> questionMap = questionsBySubCategory
                                                .get(subCategoryId);

                                if (questionMap == null)
                                        continue;

                                Map<String, Object> answerDetail = new HashMap<>();

                                // 카테고리 및 서브카테고리 정보 찾기
                                Long categoryId = subCategoryToCategoryMap.get(subCategoryId);
                                if (categoryId != null) {
                                        Category category = categoryMap.get(categoryId);
                                        answerDetail.put("categoryName", category.getName());
                                        answerDetail.put("categoryQuestion", category.getCategoryQuestion()); // L0 질문

                                        // 서브카테고리명 추가
                                        SubCategory subCategory = subCategoryMap.get(subCategoryId);
                                        if (subCategory != null) {
                                                answerDetail.put("subCategoryName", subCategory.getName());
                                        }
                                }

                                QuestionInfo questionInfo = questionMap.get(QuestionLevel.valueOf(level));
                                if (questionInfo != null) {
                                        answerDetail.put("subCategoryId", subCategoryId);
                                        answerDetail.put("level", level);
                                        answerDetail.put("question", questionInfo.getContent());

                                        if (LEVEL_L1.equals(level) || LEVEL_L2.equals(level)
                                                        || LEVEL_L3.equals(level)) {
                                                // 선택형 답변: 옵션 내용 찾기
                                                Long optionId = answer.getOptionId();
                                                if (optionId != null) {
                                                        String optionContent = questionInfo.getOptions().stream()
                                                                        .filter(opt -> opt.getOptionId()
                                                                                        .equals(optionId))
                                                                        .map(QuestionOptionInfo::getContent)
                                                                        .findFirst()
                                                                        // TODO: 에러 메시지를 상수나 예외로 처리
                                                                        .orElse("선택한 옵션을 찾을 수 없습니다");
                                                        answerDetail.put("selectedOption", optionContent);
                                                }
                                        } else if (LEVEL_L4.equals(level)) {
                                                // 서술형 답변
                                                answerDetail.put("textAnswer", answer.getTextAnswer());
                                        }

                                        // 단계별로 분류
                                        // TODO: 이 분류 로직을 별도 메서드로 분리
                                        if (LEVEL_L1.equals(level)) {
                                                stage1Data.add(answerDetail);
                                        } else if (LEVEL_L2.equals(level) || LEVEL_L3.equals(level)) {
                                                stage2Data.add(answerDetail);
                                        } else if (LEVEL_L4.equals(level)) {
                                                stage3Data.add(answerDetail);
                                        }
                                }
                        }

                        // ✅ 4. 프롬프트 구성
                        // TODO: 프롬프트 템플릿을 별도 파일이나 상수 클래스로 분리하여 관리
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

                        // ✅ 5. 요청 본문 생성
                        String requestJson = """
                                        {
                                                              "model": "%s",
                                          "messages": [
                                                                {"role":"system","content":"You are a helpful assistant that returns card information based on users questionairre answers."},
                                            {"role":"user","content": %s}
                                          ],
                                                              "temperature": %s
                                        }
                                                            """
                                        .formatted(GPT_MODEL, objectMapper.writeValueAsString(prompt), TEMPERATURE);

                        // ✅ 6. HTTP 헤더 및 요청
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.setBearerAuth(openAiApiKey);

                        // TODO: RestTemplate을 Bean으로 등록하여 재사용 (매번 생성하는 것은 비효율적)
                        RestTemplate restTemplate = new RestTemplateBuilder()
                                        .connectTimeout(CONNECT_TIMEOUT)
                                        .readTimeout(READ_TIMEOUT)
                                        .build();

                        ResponseEntity<String> response = restTemplate.exchange(
                                        OPENAI_URL,
                                        HttpMethod.POST,
                                        new HttpEntity<>(requestJson, headers),
                                        String.class);

                        log.info("📡 GPT 응답 코드: {}", response.getStatusCode());
                        // TODO: 응답 바디 로깅 시 민감한 정보가 포함될 수 있으므로 제거하거나 마스킹 처리
                        log.info("📡 GPT 응답 바디: {}", response.getBody());

                        if (response.getStatusCode() != HttpStatus.OK) {
                                return OnboardingInfo.CardGeneration.failed();
                        }

                        // ✅ 7. 응답 파싱
                        // TODO: JSON 파싱 시 더 구체적인 예외 처리 (JsonProcessingException 등)
                        // TODO: choices 배열이 비어있거나 null인 경우 처리 추가
                        String raw = response.getBody();
                        JsonNode root = objectMapper.readTree(raw);
                        String content = root.path("choices").get(0).path("message").path("content").asText();
                        JsonNode cardJson = objectMapper.readTree(content);

                        // ✅ 8. 플레이스홀더 치환을 위한 카테고리/서브카테고리/옵션 매핑 생성
                        Map<String, String> replacementMap = buildReplacementMap(
                                        process, categoryMap, subCategoryMap, questionsBySubCategory);

                        // TODO: StageCard 빌더 반복 코드를 별도 메서드로 분리 (buildStageCard(JsonNode, String
                        // stageName))
                        StageCard s1 = StageCard.builder()
                                        .title(replacePlaceholders(
                                                        cardJson.path("stage1").path("title").asText(""),
                                                        replacementMap))
                                        .subtitle(replacePlaceholders(
                                                        cardJson.path("stage1").path("subtitle").asText(""),
                                                        replacementMap))
                                        .content(replacePlaceholders(
                                                        cardJson.path("stage1").path("content").asText(""),
                                                        replacementMap))
                                        .build();

                        StageCard s2 = StageCard.builder()
                                        .title(replacePlaceholders(
                                                        cardJson.path("stage2").path("title").asText(""),
                                                        replacementMap))
                                        .subtitle(replacePlaceholders(
                                                        cardJson.path("stage2").path("subtitle").asText(""),
                                                        replacementMap))
                                        .content(replacePlaceholders(
                                                        cardJson.path("stage2").path("content").asText(""),
                                                        replacementMap))
                                        .build();

                        StageCard s3 = StageCard.builder()
                                        .title(replacePlaceholders(
                                                        cardJson.path("stage3").path("title").asText(""),
                                                        replacementMap))
                                        .subtitle(replacePlaceholders(
                                                        cardJson.path("stage3").path("subtitle").asText(""),
                                                        replacementMap))
                                        .content(replacePlaceholders(
                                                        cardJson.path("stage3").path("content").asText(""),
                                                        replacementMap))
                                        .build();

                        return OnboardingInfo.CardGeneration.done(s1, s2, s3);

                } catch (Exception e) {
                        // TODO: 예외 타입별 구체적인 처리 (RestClientException, JsonProcessingException 등)
                        log.error("❌ GPT API 호출 실패", e);
                        return OnboardingInfo.CardGeneration.failed();
                }
        }

        /**
         * 플레이스홀더 치환을 위한 매핑 생성
         */
        private Map<String, String> buildReplacementMap(
                        OnboardingProcess process,
                        Map<Long, Category> categoryMap,
                        Map<Long, SubCategory> subCategoryMap,
                        Map<Long, Map<QuestionLevel, QuestionInfo>> questionsBySubCategory) {
                Map<String, String> replacementMap = new HashMap<>();

                // 카테고리명 매핑 - CategoryId enum에서 읽어오기
                String categoryName = null;
                for (Long categoryId : categoryMap.keySet()) {
                        categoryName = CategoryId.getNameById(categoryId);
                        if (categoryName != null) {
                                // 한글 플레이스홀더
                                replacementMap.put("[카테고리명]", categoryName);
                                replacementMap.put("[" + categoryName + "]", categoryName);
                                // 영어 플레이스홀더
                                replacementMap.put("[categoryName]", categoryName);
                                replacementMap.put("**[categoryName]**", categoryName);
                        } else {
                                // enum에 없는 경우 DB에서 가져온 값 사용
                                Category category = categoryMap.get(categoryId);
                                if (category != null) {
                                        categoryName = category.getName();
                                        replacementMap.put("[카테고리명]", categoryName);
                                        replacementMap.put("[" + categoryName + "]", categoryName);
                                        replacementMap.put("[categoryName]", categoryName);
                                        replacementMap.put("**[categoryName]**", categoryName);
                                }
                        }
                }

                // 서브카테고리명 및 옵션 매핑
                Set<String> l2Options = new HashSet<>();
                Set<String> l3Options = new HashSet<>();
                // TODO : subcatgoryName, selectedOption 추후 제거 예정
                String subCategoryName = null;
                String selectedOption = null;
                String textAnswer = null;

                for (OnboardingSubCategoryAnswers answer : process.getSubCategoryAnswers()) {
                        Long subCategoryId = answer.getSubCategoryId();
                        String level = answer.getLevel();
                        SubCategory subCategory = subCategoryMap.get(subCategoryId);

                        if (subCategory != null) {
                                subCategoryName = subCategory.getName();
                                // 한글 플레이스홀더
                                replacementMap.put("[서브카테고리명]", subCategoryName);
                                // 영어 플레이스홀더
                                replacementMap.put("[subCategoryName]", subCategoryName);
                                replacementMap.put("**[subCategoryName]**", subCategoryName);
                        }

                        Map<QuestionLevel, QuestionInfo> questionMap = questionsBySubCategory.get(subCategoryId);
                        if (questionMap != null) {
                                QuestionInfo questionInfo = questionMap.get(QuestionLevel.valueOf(level));
                                if (questionInfo != null) {
                                        if (answer.getOptionId() != null) {
                                                String optionContent = questionInfo.getOptions().stream()
                                                                .filter(opt -> opt.getOptionId()
                                                                                .equals(answer.getOptionId()))
                                                                .map(QuestionOptionInfo::getContent)
                                                                .findFirst()
                                                                .orElse("");

                                                if (!optionContent.isEmpty()) {
                                                        selectedOption = optionContent;
                                                        // 영어 플레이스홀더
                                                        replacementMap.put("[selectedOption]", selectedOption);
                                                        replacementMap.put("**[selectedOption]**", selectedOption);

                                                        if (LEVEL_L2.equals(level)) {
                                                                l2Options.add(optionContent);
                                                        } else if (LEVEL_L3.equals(level)) {
                                                                l3Options.add(optionContent);
                                                        }
                                                }
                                        } else if (LEVEL_L4.equals(level) && answer.getTextAnswer() != null) {
                                                textAnswer = answer.getTextAnswer();
                                                // 영어 플레이스홀더
                                                replacementMap.put("[textAnswer]", textAnswer);
                                                replacementMap.put("**[textAnswer]**", textAnswer);
                                        }
                                }
                        }
                }

                // L2, L3 옵션을 쉼표로 구분하여 추가
                if (!l2Options.isEmpty()) {
                        String l2OptionsStr = String.join(", ", l2Options);
                        replacementMap.put("[L2 옵션]", l2OptionsStr);
                        replacementMap.put("**[L2 옵션]**", l2OptionsStr);
                }
                if (!l3Options.isEmpty()) {
                        String l3OptionsStr = String.join(", ", l3Options);
                        replacementMap.put("[L3 옵션]", l3OptionsStr);
                        replacementMap.put("**[L3 옵션]**", l3OptionsStr);
                }

                log.info("🔧 플레이스홀더 치환 맵: {}", replacementMap);
                return replacementMap;
        }

        /**
         * 플레이스홀더를 실제 값으로 치환
         */
        private String replacePlaceholders(String text, Map<String, String> replacementMap) {
                if (text == null || text.isEmpty()) {
                        return text;
                }

                String result = text;
                // 모든 플레이스홀더를 치환 (여러 번 나타날 수 있으므로 replaceAll 사용)
                for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
                        String placeholder = entry.getKey();
                        String value = entry.getValue();
                        if (value != null) {
                                result = result.replace(placeholder, value);
                        }
                }

                log.info("🔧 치환 전: {}, 치환 후: {}", text, result);
                return result;
        }
}