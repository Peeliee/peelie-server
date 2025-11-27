package com.peelie.onboarding.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.openai.models.responses.*;
import com.peelie.onboarding.domain.CardGenerator;
import com.peelie.onboarding.domain.GeneratedCardPayload;
import com.peelie.onboarding.domain.OnboardingData;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.responses.ResponseCreateParams;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.ResponseFormatJsonSchema;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.core.JsonValue;
import com.openai.models.responses.ResponseFormatTextConfig;
import com.openai.models.responses.ResponseFormatTextJsonSchemaConfig;
import com.openai.models.responses.ResponseTextConfig;
import com.openai.models.*;
import com.openai.models.responses.ResponseInputItem;

@Slf4j
@Service
public class CardGeneratorImpl implements CardGenerator {
    private final OpenAIClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public CardGeneratorImpl(OpenAIClient openAIClient, ObjectMapper objectMapper) {
        this.client = openAIClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public GeneratedCardPayload generateCard(OnboardingData data) throws JsonProcessingException {
        try {
            // 1.GPT 5버전도 여전히 ObjectMapper 필요-> OnboardingData를 JSON 문자열로 변환
            String promptUser = "아래 온보딩 정보로 세 단계 카드 msg를 만들어줘.Json으로 반환해줘.\n " +
                    objectMapper.writeValueAsString(data);
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


//            ResponseInputItem userMessageItem = ResponseInputItem
//                    .messages(
//                            ResponseInputItem.Message.builder()
//                                    .role(ResponseInputItem.Message.Role.USER) // 👈 "이건 사용자의 명령이야"라고 명시
//                                    .content(prompt)
//                                    .build()
//                    )
//                    .build();
            // 1. 응답 형식 스키마 정의
            Map<String, Object> stageCardSchema = Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "title", Map.of("type", "string"),
                            "subtitle", Map.of("type", "string"),
                            "content", Map.of("type", "string")
                    ),
                    "required", List.of("title", "subtitle", "content"), // 필수 필드
                    "additionalProperties", false // 딴소리 금지
            );

            // 전체 구조 (GeneratedCardPayload)
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

//            ResponseFormatJsonSchema.JsonSchema.Schema.Builder schemaBuilder =
//                    ResponseFormatJsonSchema.JsonSchema.Schema.builder();


            // Map의 각 항목을 JsonValue로 감싸서 빌더에 넣습니다.
//

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
            // 4. Response에서 JSON 문자열 추출

            // response.toString() 또는 response.data() 등으로 JSON 문자열을 받아야 함
//            String responseContent = response.toString();
            String a = response.toString();
            System.out.println("a = " + a);
            log.info("OpenAI Response: " + a);
            String resultTest = response._output().asStringOrThrow();
            System.out.println("resultTest = " + resultTest);
            return objectMapper.readValue(resultTest, GeneratedCardPayload.class);
            // 5. JSON String -> GeneratedCardPayload 변환 (ObjectMapper 필요)
//            GeneratedCardPayload payload = objectMapper.readValue(responseContent, GeneratedCardPayload.class);
//
//            return payload;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 처리 에러",e);
        } catch (Exception e) {
            throw new RuntimeException("OPEN AI API 호출 실패",e);
        }
    }
}
