package com.peelie.onboarding.domain;

import com.openai.client.OpenAIClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CardGenerator {

    CompletableFuture<OnboardingInfo.CardGeneration> generateCard(Long userId, List<Long> categoryIds);
}
