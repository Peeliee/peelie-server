package com.peelie.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponse<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T data;

    public static <T> SuccessResponse<T> of(int status, String message, T data) {
        return new SuccessResponse<>(status, true, message, data);
    }

    public static <T> SuccessResponse<T> of(HttpStatus status, String message, T data) {
        return of(status.value(), message, data);
    }

    public static <T> SuccessResponse<T> ok(T data) {
        return of(HttpStatus.OK, "OK", data);
    }

    public static <T> SuccessResponse<T> created(T data) {
        return of(HttpStatus.CREATED, "Created", data);
    }
}
