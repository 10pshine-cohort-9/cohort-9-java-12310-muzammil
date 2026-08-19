package com._pearls.cms.service;

import com._pearls.cms.dto.request.LoginRequest;
import com._pearls.cms.dto.request.RegisterRequest;
import com._pearls.cms.dto.request.ResendOtpRequest;
import com._pearls.cms.dto.request.VerifyOtpRequest;
import com._pearls.cms.dto.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse verifyRegistrationOtp(VerifyOtpRequest request);

    void resendRegistrationOtp(ResendOtpRequest request);

    AuthResponse login(LoginRequest request);
}
