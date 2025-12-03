package com.peelie.onboarding.domain.card;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.CompletableFuture;

public interface CardGenerator {

     CompletableFuture<GeneratedCardPayload> generateCard(CardOnboardingData data);
     BioResponse generateIntroWithCard(int cardStageNo);
}
