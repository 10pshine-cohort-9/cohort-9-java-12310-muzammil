package com._pearls.cms.service.impl;

import com._pearls.cms.common.exception.ApiException;
import com._pearls.cms.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import com._pearls.cms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@contactmanagement.com}")
    private String fromEmail;

    @Override
    public void sendOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.isBlank()) {
                message.setFrom(fromEmail);
            }
            message.setTo(to);
            message.setSubject("Account Verification OTP");
            message.setText("Your OTP for account verification is: " + otp + "\nThis OTP will expire in 5 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", maskEmail(to), e); // Deliberately avoiding logging the raw OTP
            throw new ApiException(
                "Unable to send verification email. Please try again later.",
                ErrorCode.EMAIL_DELIVERY_FAILED,
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String name = parts[0];
        if (name.length() <= 2) {
            return "***@" + parts[1];
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + "@" + parts[1];
    }
}
