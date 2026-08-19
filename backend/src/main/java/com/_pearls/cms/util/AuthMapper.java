package com._pearls.cms.util;

import com._pearls.cms.dto.request.RegisterRequest;
import com._pearls.cms.dto.response.AuthResponse;
import com._pearls.cms.entity.User;

public final class AuthMapper {

    private AuthMapper() {
    }

    public static User toUser(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(encodedPassword)
                .build();
    }

    public static AuthResponse toAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .token(token)
                .build();
    }
}
