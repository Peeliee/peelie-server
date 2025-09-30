package com.peelie.onboarding.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OnboardingAnswer {
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "answer_value", nullable = false, length = 1000)
    private String value;
}