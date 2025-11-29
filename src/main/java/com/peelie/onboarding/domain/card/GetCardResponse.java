package com.peelie.onboarding.domain.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetCardResponse {
    private final String status;
    private final CardInfo data;

    public static final String STATUS_GENERATED = "GENERATED";
}