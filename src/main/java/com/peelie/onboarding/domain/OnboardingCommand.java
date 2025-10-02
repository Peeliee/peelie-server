package com.peelie.onboarding.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

public class OnboardingCommand {

    @Getter
    @Builder
    @ToString
    public static class SelectCategories {
        private Long userId;
        private Set<Long> categoryIds;
    }


}