package com._pearls.cms.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends ApiException {
    public DuplicateResourceException(String message) {
        super(message, ErrorCode.DUPLICATE_RESOURCE, HttpStatus.CONFLICT);
    }
}
