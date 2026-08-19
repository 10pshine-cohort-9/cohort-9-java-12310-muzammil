package com._pearls.cms.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final Long id;
    private final String email;
    private final String phoneNumber;
    private final String token;
}
