package com.peelie.onboarding.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.openai.models.responses.*;
import com.peelie.onboarding.domain.card.CardGenerator;
import com.peelie.onboarding.domain.card.GeneratedCardPayload;
import com.peelie.onboarding.domain.card.CardOnboardingData;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.core.JsonValue;


@Slf4j
@Service
public class CardGeneratorImpl implements CardGenerator {
    private final Executor customExecutor;
    private final OpenAIClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public CardGeneratorImpl(OpenAIClient openAIClient, ObjectMapper objectMapper,Executor customExecutor) {
        this.client = openAIClient;
        this.objectMapper = objectMapper;
        this.customExecutor = customExecutor;
    }

    @Override
    public CompletableFuture<GeneratedCardPayload> generateCard(CardOnboardingData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
            //  GPT 5버전도 여전히 ObjectMapper 필요-> OnboardingData를 JSON 문자열로 변환
            String promptUser = "아래 온보딩 정보로 세 단계 카드 msg를 만들어줘.Json으로 반환해줘.\n " +
                    objectMapper.writeValueAsString(data); // higu
            String systemPrompt = "Instead, aggregate all generated cards from all categories into a single value for each respective stage.\n" +
                    "Strictly adhere to the defined JSON Schema (DTO) provided in the API call.";

            List<ResponseInputItem> inputs = new ArrayList<>();

            ResponseInputItem userMessageItem = ResponseInputItem.ofMessage(
                    ResponseInputItem.Message.builder()
                            .role(ResponseInputItem.Message.Role.USER)
                            .addInputTextContent(promptUser)  // 여기에 프롬프트 내용
                            .build()
            );
            ResponseInputItem systemMessageItem = ResponseInputItem.ofMessage(
                    ResponseInputItem.Message.builder()
                            .role(ResponseInputItem.Message.Role.SYSTEM)
                            .addInputTextContent(systemPrompt)
                            .build()
            );
            inputs.add(userMessageItem);
            inputs.add(systemMessageItem);


            // 1. 응답 형식 stage내부 스키마 정의
            Map<String, Object> stageCardSchema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "title", Map.of("type", "string"),
                            "subtitle", Map.of("type", "string"),
                            "content", Map.of("type", "string")
                    ),
                    "required", List.of("title", "subtitle", "content"), // 필수 필드
                    "additionalProperties", false
            );

                //  2. 전체 구조 스키마 정의
            Map<String, Object> rootSpec = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "stage1", stageCardSchema,
                            "stage2", stageCardSchema,
                            "stage3", stageCardSchema
                    ),
                    "required", List.of("stage1", "stage2", "stage3"),
                    "additionalProperties", false
            );


            ResponseFormatTextJsonSchemaConfig.Schema.Builder schemaBuilder =
                    ResponseFormatTextJsonSchemaConfig.Schema.builder();
            rootSpec.forEach((k, v) -> {
                schemaBuilder.putAdditionalProperty(k, JsonValue.from(v));
            });

            ResponseFormatTextJsonSchemaConfig jsonSchemaConfig =
                    ResponseFormatTextJsonSchemaConfig.builder()
                            .name("generated_card_payload")
                            .schema(schemaBuilder.build())
                            .strict(true)
                            .build();

            ResponseTextConfig textConfig = ResponseTextConfig.builder()
                    .format(
                            ResponseFormatTextConfig.ofJsonSchema(jsonSchemaConfig)
                    )
                    .build();

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_5_1_CHAT_LATEST)
                    .input(ResponseCreateParams.Input.ofResponse(inputs))
                    .text(textConfig)
                    .build();


            // 3. OpenAI Response API 호출
            Response response = client.responses().create(params);


                // 4.  Response에서 필요한 응답만 추출
                String resultJson = extractJsonString(response);
                System.out.println("resultJson = " + resultJson);
                GeneratedCardPayload payload = objectMapper.readValue(resultJson, GeneratedCardPayload.class);
                return payload;


        } catch (Exception e) {
                throw new RuntimeException("카드 생성 중 오류", e);    // supplyAsync 안에선 이렇게 예외 던짐
        }
        }, customExecutor);
    }


    /*** [Util Method]
     * GPT 5.1은 reasoning블록 먼저 반환 후에 message를 반환
     *
     *  @param response GPT Response 객체
     * @return  message 타입만 쏙 골라내서 텍스트를 추출합니
     */
    private String extractJsonString(Response response) {
        // 1. Response 객체를 통째로 JSON 트리(Node)로 변환
        // (SDK의 엄격한 타입 검사를 우회하기 위함입니다)
        JsonNode rootNode = objectMapper.valueToTree(response);
        log.info(" [DEBUG] GPT 전체 응답: " + rootNode.toPrettyString());
        // 2. "output" 배열 접근
        JsonNode outputNode = rootNode.path("output");
        if (outputNode.isMissingNode() || outputNode.isEmpty()) {
            throw new RuntimeException("GPT 응답에 'output' 필드 부재");
        }
        JsonNode targetMessage = null;

        for (JsonNode item : outputNode) {
            String type = item.path("type").asText();
            if ("message".equals(type)) {
                targetMessage = item;
                break;
            }
        }

        if (targetMessage == null) {
            throw new RuntimeException();
        }
        //  context내부 텍스트 추출
        JsonNode contentNode = targetMessage.path("content");
        if (contentNode.isMissingNode() || contentNode.isEmpty()) {
            throw new RuntimeException("GPT 메시지에 'content'가 비어있습니다.");
        }

        for (JsonNode contentItem : contentNode) {
            if (contentItem.has("text")) {
                return contentItem.path("text").asText();
            }
        }
        throw new RuntimeException("GPT 메시지의 content에 'text' 필드가 없습니다.");
    }
}
