package com.peelie.onboarding.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.*;
import com.peelie.onboarding.domain.OnboardingInfo;
import com.peelie.onboarding.domain.OnboardingInfo.StageCard;
import com.peelie.onboarding.domain.OnboardingData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardGeneratorImpl implements CardGenerator {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    // Use string or ChatModel enum if available
    private static final ChatModel GPT_MODEL = ChatModel.GPT_4O;
    private static final double TEMPERATURE = 0.7;

    @Override
    public OnboardingInfo.CardGeneration generateCard(OnboardingData data) {
        try {
            // 1. Prepare Prompt
            String prompt = buildPrompt(data);

            // Include DTO structure in the prompt to guide the model since we use JSON_OBJECT mode
            // (This is the most robust way without using experimental Schema helpers)
            String jsonStructure = objectMapper.writeValueAsString(new GeneratedCardPayload());

            // 2. Build Request using Official Library Builders
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(GPT_MODEL)
                    .messages(List.of(
                            ChatCompletionMessageParam.ofSystem(
                                    ChatCompletionSystemMessageParam.builder()
                                            .content("You are a helpful assistant that generates structured card content based on user interview data. Output must be valid JSON matching this structure: " + jsonStructure)
                                            .build()
                            ),
                            ChatCompletionMessageParam.ofUser(
                                    ChatCompletionUserMessageParam.builder()
                                            .content(prompt)
                                            .build()
                            )
                    ))
                    .temperature(TEMPERATURE)
                    // Use JSON Object mode to ensure valid JSON response
                    .responseFormat(ResponseFormat.ofJsonObject(
                            ResponseFormatJsonObject.builder().build()
                    ))
                    .build();

            // 3. Call API
            log.info("📡 GPT API Request sent (using Official OpenAIClient).");
            ChatCompletion response = openAIClient.chat().completions().create(params);

            // 4. Parse Response
            // The official library returns Optional for content, so we handle it safely
            String content = response.choices().get(0).message().content()
                    .orElseThrow(() -> new RuntimeException("GPT returned empty content"));

            // Map JSON string to DTO
            GeneratedCardPayload payload = objectMapper.readValue(content, GeneratedCardPayload.class);

            log.info("✅ GPT API Response received and parsed.");

            // 5. Map to Domain Object
            return mapToCardGeneration(payload);

        } catch (Exception e) {
            log.error("❌ Card generation failed", e);
            throw new RuntimeException("Card generation failed", e);
        }
    }

    private String buildPrompt(OnboardingData data) throws JsonProcessingException {
        String jsonData = objectMapper.writeValueAsString(data);

        return """
                사용자의 온보딩 설문 데이터가 JSON 형식으로 주어집니다.
                이 데이터를 분석하여 사용자의 성향에 맞는 3단계(Stage 1, 2, 3) 카드를 생성해주세요.
                
                [입력 데이터]
                %s
                
                [요구사항]
                - 각 스테이지는 title, subtitle, content를 가져야 합니다.
                - 반드시 지정된 JSON 형식으로만 응답하세요.
                """.formatted(jsonData);
    }

    private OnboardingInfo.CardGeneration mapToCardGeneration(GeneratedCardPayload payload) {
        StageCard s1 = StageCard.builder()
                .title(payload.getStage1().getTitle())
                .subtitle(payload.getStage1().getSubtitle())
                .content(payload.getStage1().getContent())
                .build();

        StageCard s2 = StageCard.builder()
                .title(payload.getStage2().getTitle())
                .subtitle(payload.getStage2().getSubtitle())
                .content(payload.getStage2().getContent())
                .build();

        StageCard s3 = StageCard.builder()
                .title(payload.getStage3().getTitle())
                .subtitle(payload.getStage3().getSubtitle())
                .content(payload.getStage3().getContent())
                .build();

        return OnboardingInfo.CardGeneration.done(s1, s2, s3);
    }

    // --- DTO for JSON Mapping ---

    @Getter
    @NoArgsConstructor
    static class GeneratedCardPayload {
        @JsonProperty(required = true)
        private StageCardPayload stage1;

        @JsonProperty(required = true)
        private StageCardPayload stage2;

        @JsonProperty(required = true)
        private StageCardPayload stage3;
    }

    @Getter
    @NoArgsConstructor
    static class StageCardPayload {
        @JsonProperty(required = true)
        private String title;
        @JsonProperty(required = true)
        private String subtitle;
        @JsonProperty(required = true)
        private String content;
    }
}