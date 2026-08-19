package com._pearls.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "otp_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OtpToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder
    public OtpToken(String email, String otpHash, Instant expiresAt) {
        this.email = email;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
    }
}
