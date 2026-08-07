package com._pearls.cms.repository;

import com._pearls.cms.entity.ContactPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactPhoneRepository extends JpaRepository<ContactPhone, Long> {

    List<ContactPhone> findByContactIdAndContactUserId(Long contactId, Long userId);

    Optional<ContactPhone> findByIdAndContactIdAndContactUserId(Long id, Long contactId, Long userId);

    boolean existsByContactIdAndContactUserIdAndPhoneNumber(Long contactId, Long userId, String phoneNumber);
}
