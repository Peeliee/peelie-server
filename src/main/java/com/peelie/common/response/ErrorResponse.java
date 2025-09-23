package com.peelie.common.response;

import com.peelie.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final boolean success;
    private final String message;
    private final ErrorCode code;
    private final String reason;

    public static ErrorResponse of(int status, String message, ErrorCode code, String reason) {
        return new ErrorResponse(status, false, message, code, reason);
    }

    public static ErrorResponse of(ErrorCode code, String reason) {
        return new ErrorResponse(code.getStatus(), false, code.getErrorMsg(), code, null);
    }
}
