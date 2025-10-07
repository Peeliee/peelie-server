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

        public Process(OnboardingProcess process) {
            this.processId = process.getId();
            this.userId = process.getUserId();
            this.status = process.getStatus();
            this.selectedCategoryIds = process.getSelectedCategories();
            this.answers = process.getAnswers().stream()
                    .map(ans -> new Answer(ans.getQuestionId(), ans.getValue()))
                    .toList();
        }
    }

    @Getter
    @Builder
    public static class Answer{ //개별 답변 단위로 관리
        private Long questionId;
        private String value;

        public Answer(Long questionId, String value) {
            this.questionId = questionId;
            this.value = value;
        }
    }
}

