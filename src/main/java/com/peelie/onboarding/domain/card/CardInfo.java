package com.peelie.onboarding.domain.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class CardInfo {
    private final Stage stage1;
    private final Stage stage2;
    private final Stage stage3;

    @Getter
    @AllArgsConstructor
    public static class Stage {
        private final String title;
        private final String subtitle;
        private final String content;
    }
}
