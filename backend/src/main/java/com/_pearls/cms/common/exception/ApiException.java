package com._pearls.cms.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public class ApiException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final HttpStatus status;

    public ApiException(String message, ErrorCode errorCode, HttpStatus status) {
        super(Objects.requireNonNull(message, "Message must not be null"));
        this.errorCode = Objects.requireNonNull(errorCode, "ErrorCode must not be null");
        this.status = Objects.requireNonNull(status, "HttpStatus must not be null");
    }
}
