package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import com.peelie.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

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
    @CollectionTable(name = "onboarding_subcategory_answers",
            joinColumns = @JoinColumn(name = "onboarding_process_id"))
    private Set<OnboardingSubCategoryAnswers> subCategoryAnswers = new HashSet<>();





    public static OnboardingProcess start(Long userId) {
        if (userId == null) {
            throw new BaseException("유효하지 않은 사용자입니다.", ErrorCode.VALIDATION_ERROR);
        }
        OnboardingProcess process = new OnboardingProcess();
        process.userId = userId;
        return process;
    }


    public void setCategories(Collection<Long> ids) { //카테고리 선택 검증
        //요청 바디가 아예 비어있지 않은지 검증
        if (ids == null || ids.isEmpty()) {
            throw new BaseException("카테고리 목록이 비어 있습니다.", ErrorCode.VALIDATION_ERROR);
        }

        //중복 검증을 위해 Set으로 변환
        Set<Long> unique = new HashSet<>(ids);

        //개수 체크
        if ( unique.size() != 3) {
            throw new BaseException("카테고리는 중복 없이 정확히 3개를 선택해야 합니다.", ErrorCode.VALIDATION_ERROR);
        }

        //ids에 null이 포함되어있지 않은지 검증
        if (unique.stream().anyMatch(Objects::isNull)) {
            throw new BaseException("카테고리 ID가 유효하지 않습니다.", ErrorCode.VALIDATION_ERROR);
        }

        //카테고리 선택
        this.selectedCategories.clear();
        this.selectedCategories.addAll(unique);
    }


    public void setSubCategoryAnswers(Long subCategoryId, List<OnboardingSubCategoryAnswers> answers) {
        if (subCategoryId == null || answers == null ||  answers.isEmpty()) {
            throw new BaseException("subCategoryId 또는 answers가 유효하지 않습니다.", ErrorCode.VALIDATION_ERROR);
        }
        // 기존에 있던 동일 subCategoryId의 답변들 제거
        subCategoryAnswers.removeIf(existing -> Objects.equals(existing.getSubCategoryId(), subCategoryId));

        // 새 답변 추가
        subCategoryAnswers.addAll(answers);
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
















