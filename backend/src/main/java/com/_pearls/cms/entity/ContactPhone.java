package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.PhoneLabel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPhone extends BaseEntity {

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneLabel label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}
