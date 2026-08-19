package com._pearls.cms.service;

import com._pearls.cms.common.exception.ApiException;
import com._pearls.cms.common.exception.ErrorCode;
import com._pearls.cms.entity.OtpToken;
import com._pearls.cms.repository.OtpTokenRepository;
import com._pearls.cms.service.impl.OtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpTokenRepository otpTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl(otpTokenRepository, passwordEncoder, emailService);
    }

    @Test
    @DisplayName("generateAndSendOtp should delete previous OTP, save new hashed OTP, and send email")
    void testGenerateAndSendOtp() {
        String email = "test@example.com";
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_otp");

        otpService.generateAndSendOtp(email);

        verify(otpTokenRepository).deleteByEmail(email);

        ArgumentCaptor<OtpToken> captor = ArgumentCaptor.forClass(OtpToken.class);
        verify(otpTokenRepository).save(captor.capture());
        OtpToken savedToken = captor.getValue();

        assertEquals(email, savedToken.getEmail());
        assertEquals("hashed_otp", savedToken.getOtpHash());
        assertNotNull(savedToken.getExpiresAt());

        verify(emailService).sendOtp(eq(email), anyString());
    }

    @Test
    @DisplayName("generateAndSendOtp should propagate ApiException if email fails")
    void testGenerateAndSendOtpEmailFailure() {
        String email = "test@example.com";
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_otp");
        org.mockito.Mockito.doThrow(new ApiException("Unable to send verification email. Please try again later.", ErrorCode.EMAIL_DELIVERY_FAILED, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR))
            .when(emailService).sendOtp(eq(email), anyString());

        ApiException exception = assertThrows(ApiException.class, () -> otpService.generateAndSendOtp(email));
        assertEquals(ErrorCode.EMAIL_DELIVERY_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("verifyOtp should successfully verify and delete OTP when valid")
    void testVerifyOtpSuccess() {
        String email = "test@example.com";
        String plainOtp = "123456";
        String otpHash = "hashed_123456";

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpHash(otpHash)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();

        when(otpTokenRepository.findByEmail(email)).thenReturn(Optional.of(otpToken));
        when(passwordEncoder.matches(plainOtp, otpHash)).thenReturn(true);

        otpService.verifyOtp(email, plainOtp);

        verify(otpTokenRepository).delete(otpToken);
    }

    @Test
    @DisplayName("verifyOtp should throw ApiException when OTP not found")
    void testVerifyOtpNotFound() {
        String email = "test@example.com";
        when(otpTokenRepository.findByEmail(email)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> otpService.verifyOtp(email, "123456"));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("verifyOtp should throw ApiException and delete expired OTP")
    void testVerifyOtpExpired() {
        String email = "test@example.com";
        OtpToken expiredToken = OtpToken.builder()
                .email(email)
                .otpHash("hash")
                .expiresAt(Instant.now().minus(1, ChronoUnit.MINUTES))
                .build();

        when(otpTokenRepository.findByEmail(email)).thenReturn(Optional.of(expiredToken));

        ApiException exception = assertThrows(ApiException.class, () -> otpService.verifyOtp(email, "123456"));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("OTP has expired", exception.getMessage());
        verify(otpTokenRepository).delete(expiredToken);
    }

    @Test
    @DisplayName("verifyOtp should throw ApiException when OTP code is incorrect")
    void testVerifyOtpIncorrect() {
        String email = "test@example.com";
        String plainOtp = "111111";
        String otpHash = "hashed_222222";

        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpHash(otpHash)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();

        when(otpTokenRepository.findByEmail(email)).thenReturn(Optional.of(otpToken));
        when(passwordEncoder.matches(plainOtp, otpHash)).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> otpService.verifyOtp(email, plainOtp));
        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Invalid OTP", exception.getMessage());
    }
}
