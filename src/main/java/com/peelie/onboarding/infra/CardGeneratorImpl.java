package com.peelie.onboarding.infra;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletion;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.onboarding.domain.CardGenerator;
import com.peelie.onboarding.domain.InitCardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.peelie.onboarding.infra.OpenAiConfig.*;

@Component
@RequiredArgsConstructor
public class CardGeneratorImpl implements CardGenerator {

    private final OpenAIClient openAIClient;

    @Override
    public InitCardsResponse generateCards(String userPrompt) {

        // 1. 파라미터 빌드 (responseFormat에 클래스 타입 지정)
        StructuredChatCompletionCreateParams<InitCardsResponse> createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .maxCompletionTokens(2048)
                .responseFormat(InitCardsResponse.class) // 여기에 DTO 클래스를 지정
                .addUserMessage(CARD_SYSTEM_PROMPT + "\n" + userPrompt)
                .build();

        // 2. API 호출 및 파싱된 결과 받기
        // create() 메서드가 제네릭 타입을 인식하여 ParsedChatCompletion<InitCardsResponse>를 반환
        StructuredChatCompletion<InitCardsResponse> response = openAIClient.chat().completions().create(createParams);

        // 3. 결과 추출
        // choices() -> get(0) -> message() -> content() 가 Optional<InitCardsResponse> 형태가 됨
        return response.choices().get(0).message().content()
                .orElseThrow(() -> new BaseException("OpenAI API가 올바른 구조의 응답을 생성하지 못했습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
