package com.peelie.onboarding.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

public class OnboardingInfo {

    @Getter
    @Builder
    public static class Process{ //온보딩 프로세스 전 조회
        private Long processId;
        private Long userId;
        private OnboardingStatus status;
        private Set<Long> selectedCategoryIds;
        private List<Answer> answers;
    }

    @Getter
    @Builder
    public static class Answer{ //개별 답변 단위로 관리
        private Long categoryId;
        private Long questionId;
        private String value;
    }
}

