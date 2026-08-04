package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.EmailLabel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "contact_emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEmail extends BaseEntity {

    @NotBlank(message = "Email Address is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String emailAddress;

    @NotNull(message = "Label is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailLabel label;

    @NotNull(message = "Contact is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}
