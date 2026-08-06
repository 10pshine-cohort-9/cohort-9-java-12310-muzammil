package com._pearls.cms.common.response;

import com._pearls.cms.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final List<String> errors;
    private final ErrorCode errorCode;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data, List<String> errors, ErrorCode errorCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors != null ? List.copyOf(errors) : null;
        this.errorCode = errorCode;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode errorCode) {
        return new ApiResponse<>(false, message, null, null, errorCode);
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode errorCode, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors, errorCode);
    }
}
    