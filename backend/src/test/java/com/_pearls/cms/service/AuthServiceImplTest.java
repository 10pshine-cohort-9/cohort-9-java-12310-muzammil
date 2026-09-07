package com._pearls.cms.service;

import com._pearls.cms.common.exception.ApiException;
import com._pearls.cms.common.exception.DuplicateResourceException;
import com._pearls.cms.common.exception.ErrorCode;
import com._pearls.cms.dto.request.LoginRequest;
import com._pearls.cms.dto.request.RegisterRequest;
import com._pearls.cms.dto.request.ResendOtpRequest;
import com._pearls.cms.dto.request.VerifyOtpRequest;
import com._pearls.cms.dto.response.AuthResponse;
import com._pearls.cms.entity.User;
import com._pearls.cms.repository.UserRepository;
import com._pearls.cms.security.JwtService;
import com._pearls.cms.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private OtpService otpService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                otpService
        );
    }

    @Test
    @DisplayName("register should save unverified user and trigger OTP generation")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPhoneNumber("+923001234567");
        request.setPassword("Password123!");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(request.getPhoneNumber(), savedUser.getPhoneNumber());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.isVerified());

        verify(otpService).generateAndSendOtp(request.getEmail());
    }

    @Test
    @DisplayName("register should throw DuplicateResourceException when email already exists")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPhoneNumber("+923001234567");
        request.setPassword("Password123!");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("register should throw DuplicateResourceException when phone already exists")
    void testRegisterDuplicatePhone() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPhoneNumber("+923001234567");
        request.setPassword("Password123!");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("verifyRegistrationOtp should verify OTP, mark user as verified, and return JWT")
    void testVerifyRegistrationOtpSuccess() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setEmail("user@example.com");
        request.setOtp("123456");

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber("+923001234567")
                .password("encodedPassword")
                .build();
        user.setVerified(false);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt_token");

        AuthResponse response = authService.verifyRegistrationOtp(request);

        verify(otpService).verifyOtp(request.getEmail(), request.getOtp());
        assertTrue(user.isVerified());
        verify(userRepository).save(user);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertEquals(request.getEmail(), response.getEmail());
    }

    @Test
    @DisplayName("resendRegistrationOtp should generate and send new OTP for unverified user")
    void testResendOtpSuccess() {
        ResendOtpRequest request = new ResendOtpRequest();
        request.setEmail("user@example.com");

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber("+923001234567")
                .password("encodedPassword")
                .build();
        user.setVerified(false);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        authService.resendRegistrationOtp(request);

        verify(otpService).generateAndSendOtp(request.getEmail());
    }

    @Test
    @DisplayName("login with unverified user should trigger new OTP and throw UNVERIFIED_ACCOUNT exception")
    void testLoginUnverifiedUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password123!");

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber("+923001234567")
                .password("encodedPassword")
                .build();
        user.setVerified(false);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () -> authService.login(request));

        assertEquals(ErrorCode.UNVERIFIED_ACCOUNT, exception.getErrorCode());
        verify(otpService).generateAndSendOtp(request.getEmail());
    }

    @Test
    @DisplayName("login with verified user should return JWT token")
    void testLoginVerifiedUserSuccess() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Password123!");

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber("+923001234567")
                .password("encodedPassword")
                .build();
        user.setVerified(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("valid_jwt_token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("valid_jwt_token", response.getToken());
        assertEquals(request.getEmail(), response.getEmail());
    }

    @Test
    @DisplayName("login with bad credentials should throw AUTHENTICATION_FAILED")
    void testLoginBadCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ApiException exception = assertThrows(ApiException.class, () -> authService.login(request));
        assertEquals(ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }
}
