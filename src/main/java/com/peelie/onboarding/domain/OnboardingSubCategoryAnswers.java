package com.peelie.onboarding.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnboardingSubCategoryAnswers {

    @Column(name = "sub_category_id", nullable = false)
    private Long subCategoryId;

    @Column(name = "level", nullable = false)
    private String level;

    @Column(name = "option_id") //L1 ~ L3
    private Long optionId;

    @Column(name = "text_answer") //L4
    private String textAnswer;


    public OnboardingSubCategoryAnswers(Long subCategoryId, String level, Long optionId, String textAnswer) {
        this.subCategoryId = Objects.requireNonNull(subCategoryId);
        this.level = Objects.requireNonNull(level);
        this.optionId = optionId;
        this.textAnswer = textAnswer;
        validateByLevel(); //생성자 내부에서 자동 검증
    }

    // Set 중복 방지 - subCategoryId 와 level 이 같으면 같은 객체로 취급
    // .add를 하게 되면 자동으로 호출됨
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OnboardingSubCategoryAnswers that)) return false; //instanceof : 타입검사
        return Objects.equals(subCategoryId, that.subCategoryId) && Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() { return Objects.hash(subCategoryId, level); }

    // 레벨에 따른 검증
    private void validateByLevel() {
        if ("L4".equals(level)) {
            if (textAnswer == null || textAnswer.isBlank())
                throw new BaseException("L4는 textAnswer가 필요합니다.", ErrorCode.VALIDATION_ERROR);
            if (optionId != null)
                throw new BaseException("L4는 optionId를 허용하지 않습니다.", ErrorCode.VALIDATION_ERROR);
        } else { // L1~L3
            if (optionId == null)
                throw new BaseException(level + "은 optionId가 필요합니다.", ErrorCode.VALIDATION_ERROR);
            if (textAnswer != null && !textAnswer.isBlank())
                throw new BaseException(level + "은 textAnswer를 허용하지 않습니다.", ErrorCode.VALIDATION_ERROR);
        }
    }


}
