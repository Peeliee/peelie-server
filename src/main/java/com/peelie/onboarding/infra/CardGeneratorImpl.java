package com.peelie.onboarding.infra;

import com.openai.client.OpenAIClient;
import com.peelie.onboarding.domain.CardGenerator;
import com.peelie.onboarding.domain.OnboardingInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class CardGeneratorImpl implements CardGenerator {
    private final OpenAIClient client;
//DB 조회 로직은 여기 있으면 안됨
    public CardGeneratorImpl(OpenAIClient openAIClient) {
        this.client = openAIClient;
    }
    @Override
    public void generateCard(Long userId, List<Long> categoryIds) {
        // ChatCompeltion API 호출 로직 구현

    }
}
