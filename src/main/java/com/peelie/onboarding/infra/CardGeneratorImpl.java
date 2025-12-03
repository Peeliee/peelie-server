package com.peelie.onboarding.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.openai.models.responses.*;
import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.onboarding.domain.card.BioResponse;
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
    private static final String SYSTEM_PROMPT_CARD = """
            # Identity [Role Definition]

            당신은 사용자의 온보딩 설문 답변을 기반으로 3단계 카드를 생성하는 AI입니다. 제공된 1, 2, 3단계 데이터를 기반으로 각 카드를 생성해주세요.

            당신의 목표는 파편화된 사용자의 답변들을 연결하여, 공감 가고 설득력 있는 3단계 카드 메시지를 작성하는 것입니다.

            각 L+{number} 객체가 stage1, stage2, stage3의 각 단계별 응답으로 바꿔주는 게 역할이야

            [Reasoning Process - Chain of Thought] 결과를 생성하기 전에, 아래 단계를 거쳐 논리를 구성하세요 (출력에는 포함하지 마세요):

            답변을 작성하기 전에 다음 단계를 거쳐 생각하세요 (출력하지 않음):

            1. **Subject Identification:** 각 단계의 데이터에서 **L0(Category Name)**를 먼저 파악하세요. 이 단어는 모든 카드의 핵심 소재가 되어야 합니다. 내가 말하는 stage는 너가 생각하는 step이 아니고 stage는 우리 프로젝트의 사용자가 단계별로 입력하는 퀴즈 응답이란 프로젝트 고유 도메인을 의미해.

            2. **stage1 :** 'L0(주제)'와 'L1(기본 선호)'을 조합하여, 사용자가 이 분야에 어떤 흥미를 가졌는지 매력적인 서두를 던지세요.

            3. **stage2 :** 'L2(세부 취향)'와 'L3(경험/가치관)'를 엮어 구체적인 에피소드를 서술하세요.

            4. **stage3:** 'L4' 정보를 바탕으로, 앞으로의 활동을 응원하거나 제안하세요.

            각 stage의 카드는 3개의 필드에 categoryName이 key인 값들이 answers를 모두 반영해야 해 근데 categoryName을 key로 분리하지 말고 카테고리의 내용을 같은 stage끼리는 섞어서 줘야 해.

            # Instructions for variables used in <user_query>

            - **L0:** 사용자가 선택한 **'Category Name(관심 주제)'**입니다.
            (가장 중요한 키워드)

            - **L1~L4:** 해당 주제에 대한 사용자의 구체적인 답변입니다.
            L뒤에 붙은 숫자는 설문에서 질문이 점점 구체화되는 양상이야.

            [Output Constraints]

            1. **필수 포함:** 모든 단계의 `title`, `subtitle`, `content`에는 **L0(Category Name)의 맥락이나 단어가 자연스럽게 섞여 있어야 합니다.**

            caution important: stage1 stage2 stage3를 Key로 하는 각각의 value안에서 categoryName을 key로 분리하지 말고 카테고리의 내용을 같은 stage끼리는 섞어서 줘야 해.

            2. **Tone:** 감성적이고 세련된 문체를 사용하세요.

            3. **Format:** 오직 순수한 JSON 데이터만 반환하세요. (Markdown ```json 태그 금지)

            4 and 5 are constraints for JSON key value

            4. **Constraints for Json Key Value:**

            `stage1`, `stage2`, `stage3` stand for keys of each stage

            and each stage card had `title`, `subtitle`, `content` keys.
            """;
    private static final String SYSTEM_PROMPT_INTRO = """ 
    당신은 title, subtitle, content내용을 바탕으로 사용자의 한 줄소개를 30자 이내로 만들어주는 AI입니다
    title, subtitle, content 필드를 활용해 한 줄 소개를 만들어라
    
            cardStageNo 변수 값에 따라 stage{cardStageNo}의 StageCardPayload(title, subtitle, content)를 사용합니다.
            요구사항:
            - title, subtitle, content 내용을 활용해 사용자의 특징이 드러나는 한 줄 소개를 작성하세요.
            - 최대 30자 이내의 자연스러운 한국어 문장으로 만드세요.
            - 문장 끝에는 마침표를 붙이지 마세요.
            - 불필요한 설명 없이 한 줄만 출력하세요.
    """;


    @Autowired
    public CardGeneratorImpl(OpenAIClient openAIClient, ObjectMapper objectMapper, Executor customExecutor) {
        this.client = openAIClient;
        this.objectMapper = objectMapper;
        this.customExecutor = customExecutor;
    }

    @Override
    public CompletableFuture<GeneratedCardPayload> generateCard(CardOnboardingData data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String promptUser = data.toString();
                List<ResponseInputItem> inputs = new ArrayList<>();
                // 사용자 prompt와 시스템 prompt를 inputs라는 변수에 담음

                ResponseInputItem systemMessageItem = ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.SYSTEM)
                                .addInputTextContent(SYSTEM_PROMPT_CARD)
                                .build()
                );
                ResponseInputItem userMessageItem = ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.USER)
                                .addInputTextContent(promptUser)  // 여기에 프롬프트 내용
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

                // 3. OpenAI Response API 호출 -ChatCompletion 아닙니다
                Response response = client.responses().create(params);

                // 4.  GPT 응답에서 필요한 응답만 추출하여 GeneratedCardPayload로 변환
                String resultJson = extractJsonString(response);
                return objectMapper.readValue(resultJson, GeneratedCardPayload.class);

            } catch (Exception e) {
                throw new RuntimeException("카드 생성 중 오류", e);
            }
        }, customExecutor);
    }


    public BioResponse generateIntroWithCard(int cardStageNo) {

        // 1. userId기반으로 GeneratedCardPayload generatedCardPayload; 선언 이후 객체 할당 연산 필요 아니면 232행에서 에러 발생
        GeneratedCardPayload generatedCardPayload;
        GeneratedCardPayload.StageCardPayload stageCardPayload = new GeneratedCardPayload.StageCardPayload();
        // 2. stage 번호에 맞는 StageCardPayload 선택
        //아래 테스트 코드를 위해 AllArgsConstructor 임시 허용
        // TODO: GenerateCardPayload.java의 line 10,23 주석 활성화 후 아래 코드 주석 해제하여 테스트
        generatedCardPayload = new GeneratedCardPayload(
                new GeneratedCardPayload.StageCardPayload(
                        "당신의 관심이 시작되는 지점",
                        "가장 먼저 마음이 움직인 카테고리의 세계",
                        "처음 선택했던 관심들은 지금의 당신을 가장 솔직하게 보여주는 신호예요. 무엇에 끌리고 어떤 방향을 향해 있는지, 그 시작점을 그대로 품어 안아볼 시간이에요."
                ),
                new GeneratedCardPayload.StageCardPayload(
                        "당신 취향의 결이 드러나는 순간",
                        "선택의 이유가 당신을 설명해요",
                        "조금 더 깊이 들여다본 취향과 경험들은 당신만의 스토리를 만들어냈어요. 어떤 순간에 마음이 동했고, 어떤 가치가 중요했는지—그 모든 것이 지금의 결을 이루고 있어요."
                ),
                new GeneratedCardPayload.StageCardPayload(
                        null,
                        null,
                        null
                )
        );
//        GeneratedCardPayload.StageCardPayload stageCardPayload = switch (cardStageNo) {
//            case 1 -> payload.getStage1();
//            case 2 -> payload.getStage2();
//            case 3 -> payload.getStage3();
//            default -> throw new IllegalArgumentException("Invalid card stage number: " + cardStageNo);
//        };

        if (cardStageNo == 1) {
            stageCardPayload = generatedCardPayload.getStage1();
        }else if (cardStageNo==2){
            stageCardPayload = generatedCardPayload.getStage2();
        }else if (cardStageNo==3){
            stageCardPayload = generatedCardPayload.getStage3();
        }else {
            throw new IllegalArgumentException("잘못된 단계 no: " + cardStageNo);
        }

        if (stageCardPayload == null ||
                (isBlank(stageCardPayload.getTitle()) && isBlank(stageCardPayload.getSubtitle()) &&
                        isBlank(stageCardPayload.getContent()))) {
            throw new BaseException("선택된 카드가 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
        }


        String userPrompt = stageCardPayload.toStringContent();

        Map<String, Object> schemaMap = BioResponse.getUserIntroSchema();

        ResponseFormatTextJsonSchemaConfig.Schema.Builder schemaBuilder =
                ResponseFormatTextJsonSchemaConfig.Schema.builder();

        schemaMap.forEach((k, v) -> {

            schemaBuilder.putAdditionalProperty(k, JsonValue.from(v));
        });

        ResponseFormatTextJsonSchemaConfig jsonSchemaConfig =
                ResponseFormatTextJsonSchemaConfig.builder()
                        .name("bio_response")
                        .schema(schemaBuilder.build())
                        .strict(true)
                        .build();

        ResponseTextConfig textConfig = ResponseTextConfig.builder()
                .format(
                        ResponseFormatTextConfig.ofJsonSchema(jsonSchemaConfig)
                )
                .build();

        List<ResponseInputItem> inputs = new ArrayList<>();
        ResponseInputItem systemMessageItem = ResponseInputItem.ofMessage(
                ResponseInputItem.Message.builder()
                        .role(ResponseInputItem.Message.Role.SYSTEM)
                        .addInputTextContent(SYSTEM_PROMPT_INTRO)
                        .build()
        );

        ResponseInputItem userMessageItem = ResponseInputItem.ofMessage(
                ResponseInputItem.Message.builder()
                        .role(ResponseInputItem.Message.Role.USER)
                        .addInputTextContent(userPrompt)
                        .build()
        );
        inputs.add(userMessageItem);
        inputs.add(systemMessageItem);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_5_1_CHAT_LATEST)
                .input(ResponseCreateParams.Input.ofResponse(inputs))
                .text(textConfig)
                .build();

        Response response = client.responses().create(params);
        return parseGPTIntroResponse(response);
    }
    /*** [Util Method]
     * GPT 5.1은 reasoning블록 먼저 반환 후에 message를 반환
     *  @param response GPT Response 객체
     * @return message 타입만 쏙 골라내서 텍스트를 추출
     */
    private String extractJsonString(Response response) {
        JsonNode rootNode = objectMapper.valueToTree(response);
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
        //  content 내부 텍스트 추출
        JsonNode contentNode = targetMessage.path("content");
        if (contentNode.isMissingNode() || contentNode.isEmpty()) {
            throw new RuntimeException("응답에 'content' 필드 부재.");
        }

        for (JsonNode contentItem : contentNode) {
            if (contentItem.has("text")) {
                return contentItem.path("text").asText();
            }
        }
        throw new RuntimeException("응답 content에 'text' 필드 부재 .");
    }

    private BioResponse parseGPTIntroResponse(Response response) {
        try {
            String resultJson = extractJsonString(response);
            return objectMapper.readValue(resultJson, BioResponse.class);
        } catch (JsonProcessingException e) {

            throw new BaseException(" GPT 응답 파싱에 실패했습니다", ErrorCode.GPT_NOT_AVAILABLE, e);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

}
