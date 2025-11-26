package com.peelie.onboarding.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openai.client.OpenAIClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CardGenerator {

     GeneratedCardPayload generateCard(OnboardingData data) throws JsonProcessingException;

}
