package com._pearls.cms.controller;

import com._pearls.cms.common.response.ApiResponse;
import com._pearls.cms.dto.request.LoginRequest;
import com._pearls.cms.dto.request.RegisterRequest;
import com._pearls.cms.dto.request.ResendOtpRequest;
import com._pearls.cms.dto.request.VerifyOtpRequest;
import com._pearls.cms.dto.response.AuthResponse;
import com._pearls.cms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Registration initiated. Please verify your email with the OTP sent."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Email verified and login successful"));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendRegistrationOtp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "A new OTP has been sent to your email."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
}
