package com.peelie.quiz.exception;

import com.peelie.common.exception.BaseException;
import com.peelie.common.exception.ErrorCode;

public class QuizException extends BaseException {

    public QuizException() {
    }

    public QuizException(ErrorCode errorCode) {
        super(errorCode);
    }

    public QuizException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
