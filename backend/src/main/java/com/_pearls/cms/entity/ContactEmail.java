package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.EmailLabel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "contact_emails", uniqueConstraints = {
        @UniqueConstraint(name = "uc_contact_email", columnNames = {"contact_id", "email_address"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContactEmail extends BaseEntity {

    @NotBlank(message = "Email Address is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, length = 255)
    private String emailAddress;

    @NotNull(message = "Label is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmailLabel label;

    @NotNull(message = "Contact is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Contact contact;

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setLabel(EmailLabel label) {
        this.label = label;
    }

    @Builder
    public ContactEmail(String emailAddress, EmailLabel label) {
        this.emailAddress = emailAddress;
        this.label = label;
    }
}
