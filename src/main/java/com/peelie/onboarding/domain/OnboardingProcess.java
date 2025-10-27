package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingProcess extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ElementCollection
    @CollectionTable(name = "onboarding_selected_categories",
            joinColumns = @JoinColumn(name = "onboarding_process_id"))
    @Column(name = "category_id")
    private Set<Long> selectedCategories = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "onboarding_answers",
            joinColumns = @JoinColumn(name = "onboarding_process_id"))
    private List<OnboardingAnswer> answers = new ArrayList<>();

    public static OnboardingProcess start(Long userId) {
        if (userId == null) {
            throw new BaseException("유효하지 않은 사용자입니다.", ErrorCode.VALIDATION_ERROR);
        }
        OnboardingProcess process = new OnboardingProcess();
        process.userId = userId;
        return process;
    }

    public void setCategories(Set<Long> ids) { //카테고리 선택 검증
        //개수,중복 체크
        if (ids == null || ids.size() != 3) {
            throw new BaseException("카테고리는 중복 없이 정확히 3개를 선택해야 합니다.", ErrorCode.VALIDATION_ERROR);
        }

        //카테고리 선택
        this.selectedCategories.clear();
        this.selectedCategories.addAll(ids);
    }

    public void setAnswers(Long questionId, String answerValue) { //답변 선택 검증
        //입력값 검증
        if (questionId == null) {
            throw new BaseException("질문 ID가 유효하지 않습니다.", ErrorCode.VALIDATION_ERROR);
        }
        if (answerValue == null || answerValue.isBlank()) {
            throw new BaseException("답변이 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
        }

        OnboardingAnswer answer = new OnboardingAnswer(questionId, answerValue);
        this.answers.add(answer);
    }

    public void setInteractionStyle(String interactionStyle, String bio) {
        // 교류 성향 값 검증
        if (interactionStyle == null || interactionStyle.isBlank()) {
            throw new BaseException("교류 성향이 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
        }

        // 한 줄 소개 검증
        if (bio == null || bio.isBlank()) {
            throw new BaseException("한 줄 소개가 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
        }
    }
}
















