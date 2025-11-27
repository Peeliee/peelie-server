package com.peelie.quiz.domain;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizStage {
    STAGE_1(1, "1단계", "L0, L1 정보 사용"),
    STAGE_2(2, "2단계", "L0, L1, L2, L3 정보 사용"),
    STAGE_3(3, "3단계", "L0, L1, L2, L3, L4 정보 사용");

    private final int value;
    private final String displayName;
    private final String description;

    public static QuizStage fromValue(int value) {
        for (QuizStage stage : values()) {
            if (stage.value == value) {
                return stage;
            }
        }
        throw new BaseException("유효하지 않은 stage value 입니다." + value, ErrorCode.VALIDATION_ERROR);
    }
}

