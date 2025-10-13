package com.peelie.common.exception;

public class AuthException extends BaseException{
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
