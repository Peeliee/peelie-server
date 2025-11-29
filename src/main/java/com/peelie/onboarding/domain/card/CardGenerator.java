package com.peelie.onboarding.domain.card;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.CompletableFuture;

public interface CardGenerator {

     CompletableFuture<GeneratedCardPayload> generateCard(OnboardingData data) throws JsonProcessingException;

}
