package com._pearls.cms.repository;

import com._pearls.cms.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM OtpToken o WHERE o.email = :email")
    void deleteByEmail(String email);
}
