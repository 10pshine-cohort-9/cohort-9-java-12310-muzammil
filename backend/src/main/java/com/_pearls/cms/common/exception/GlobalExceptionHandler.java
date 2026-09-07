package com._pearls.cms.common.exception;

import com._pearls.cms.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        log.warn("API Exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        return createErrorResponse(ex.getMessage(), ex.getErrorCode(), ex.getStatus(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Database integrity violation: {}", ex.getMessage());
        return createErrorResponse("Database constraint violation or duplicate data", ErrorCode.DUPLICATE_RESOURCE, HttpStatus.CONFLICT, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .toList();
        return createErrorResponse("Validation failed", ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .distinct()
                .toList();
        return createErrorResponse("Validation failed", ErrorCode.VALIDATION_FAILED, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return createErrorResponse("Invalid request body", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        String message = String.format("Content-Type '%s' is not supported. Please use 'application/json'", ex.getContentType());
        return createErrorResponse(message, ErrorCode.BAD_REQUEST, HttpStatus.UNSUPPORTED_MEDIA_TYPE, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());
        return createErrorResponse(message, ErrorCode.BAD_REQUEST, HttpStatus.METHOD_NOT_ALLOWED, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("Required request parameter '%s' is missing", ex.getParameterName());
        return createErrorResponse(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value for parameter '%s'", ex.getName());
        return createErrorResponse(message, ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        return createErrorResponse("An unexpected error occurred", ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    private ResponseEntity<ApiResponse<Void>> createErrorResponse(
            String message, ErrorCode errorCode, HttpStatus status, List<String> errors) {
        ApiResponse<Void> response = (errors == null || errors.isEmpty())
                ? ApiResponse.error(message, errorCode)
                : ApiResponse.error(message, errorCode, errors);
        return ResponseEntity.status(status).body(response);
    }
}
