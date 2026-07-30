package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.EmailLabel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact_emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEmail extends BaseEntity {

    @Column(nullable = false)
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailLabel label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}
