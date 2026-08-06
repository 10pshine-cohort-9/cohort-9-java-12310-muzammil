package com._pearls.cms.repository;

import com._pearls.cms.entity.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {

    List<ContactEmail> findByContactIdAndContactUserId(Long contactId, Long userId);

    Optional<ContactEmail> findByIdAndContactIdAndContactUserId(Long id, Long contactId, Long userId);

    boolean existsByContactIdAndContactUserIdAndEmailAddress(Long contactId, Long userId, String emailAddress);
}
