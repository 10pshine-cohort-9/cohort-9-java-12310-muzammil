package com._pearls.cms.service.impl;

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
import com._pearls.cms.service.AuthService;
import com._pearls.cms.service.OtpService;
import com._pearls.cms.util.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }
        
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = AuthMapper.toUser(request, encodedPassword);
        
        userRepository.save(user);
        otpService.generateAndSendOtp(user.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse verifyRegistrationOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND_MESSAGE, ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (user.isVerified()) {
            throw new ApiException("User is already verified", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        otpService.verifyOtp(request.getEmail(), request.getOtp());

        user.setVerified(true);
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthMapper.toAuthResponse(user, jwtToken);
    }

    @Override
    public void resendRegistrationOtp(ResendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(USER_NOT_FOUND_MESSAGE, ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (user.isVerified()) {
            throw new ApiException("User is already verified", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        otpService.generateAndSendOtp(user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ApiException(USER_NOT_FOUND_MESSAGE, ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND));
            
            if (!user.isVerified()) {
                otpService.generateAndSendOtp(user.getEmail());
                throw new ApiException("Account verification required. A new OTP has been sent to your email.", ErrorCode.UNVERIFIED_ACCOUNT, HttpStatus.FORBIDDEN);
            }
            
            String jwtToken = jwtService.generateToken(user.getId(), user.getEmail());
            return AuthMapper.toAuthResponse(user, jwtToken);
            
        } catch (BadCredentialsException e) {
            throw new ApiException("Invalid email or password", ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            throw new ApiException("Authentication failed", ErrorCode.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        }
    }
}
