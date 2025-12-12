package com.peelie.onboarding.infra;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;
import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.onboarding.domain.CardBioGenerator;
import com.peelie.onboarding.domain.GeneratedCardBio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.peelie.onboarding.infra.OpenAIConfig.CARD_BIO_SYSTEM_PROMPT;

@Component
@RequiredArgsConstructor
public class GptCardBioGenerator implements CardBioGenerator {

    private final OpenAIClient openAIClient;

    @Override
    public GeneratedCardBio generate(String userPrompt) {

        StructuredResponseCreateParams<GeneratedCardBio> params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4O_MINI)
                .input(CARD_BIO_SYSTEM_PROMPT + "\n" + userPrompt)
                .text(GeneratedCardBio.class)
                .maxOutputTokens(2048)
                .temperature(0.0)
                .build();

        StructuredResponse<GeneratedCardBio> response = openAIClient.responses().create(params);

        return response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new BaseException("OpenAI API 응답 처리 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
