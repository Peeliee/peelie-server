package com.peelie.quiz.exception;

import com.peelie.common.exception.ErrorCode;

public class QuizNotFoundException extends QuizException {

    public QuizNotFoundException() {
        super("해당 퀴즈가 존재하지 않습니다.", ErrorCode.NOT_FOUND);
    }

    public QuizNotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }
}
