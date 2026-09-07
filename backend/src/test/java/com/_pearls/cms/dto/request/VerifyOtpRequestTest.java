package com._pearls.cms.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerifyOtpRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should pass validation for a valid exactly 6-digit OTP")
    void testValidOtp() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("123456");

        Set<ConstraintViolation<VerifyOtpRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    @DisplayName("Should fail validation for OTP less than 6 digits")
    void testOtpTooShort() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("12345");

        Set<ConstraintViolation<VerifyOtpRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("OTP must be exactly 6 digits")));
    }

    @Test
    @DisplayName("Should fail validation for OTP more than 6 digits")
    void testOtpTooLong() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("1234567");

        Set<ConstraintViolation<VerifyOtpRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("OTP must be exactly 6 digits")));
    }

    @Test
    @DisplayName("Should fail validation for OTP containing non-digits")
    void testOtpWithLetters() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("1234a6");

        Set<ConstraintViolation<VerifyOtpRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("OTP must be exactly 6 digits")));
    }

    @Test
    @DisplayName("Should fail validation when OTP is blank")
    void testOtpBlank() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("");

        Set<ConstraintViolation<VerifyOtpRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        // Could be the @NotBlank or the @Pattern that fails, but there should be a violation
    }
}
