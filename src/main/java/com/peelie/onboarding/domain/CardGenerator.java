package com.peelie.onboarding.domain;

public interface CardGenerator {
    InitCardsResponse generateCards(String userPrompt);
}