package com._pearls.cms.service.impl;

import com._pearls.cms.common.exception.ApiException;
import com._pearls.cms.common.exception.ErrorCode;
import com._pearls.cms.entity.OtpToken;
import com._pearls.cms.repository.OtpTokenRepository;
import com._pearls.cms.service.EmailService;
import com._pearls.cms.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    @Override
    public void generateAndSendOtp(String email) {
        otpTokenRepository.deleteByEmail(email);
        
        String plainOtp = String.format("%06d", random.nextInt(1000000));
        String otpHash = passwordEncoder.encode(plainOtp);
        
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpHash(otpHash)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();
                
        otpTokenRepository.saveAndFlush(otpToken);
        emailService.sendOtp(email, plainOtp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String plainOtp) {
        OtpToken otpToken = otpTokenRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("OTP not found or expired", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST));

        if (otpToken.getExpiresAt().isBefore(Instant.now())) {
            otpTokenRepository.delete(otpToken);
            throw new ApiException("OTP has expired", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(plainOtp, otpToken.getOtpHash())) {
            throw new ApiException("Invalid OTP", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        int deleted = otpTokenRepository.deleteByIdAndReturnCount(otpToken.getId());
        if (deleted == 0) {
            throw new ApiException("OTP has already been used", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }
}
