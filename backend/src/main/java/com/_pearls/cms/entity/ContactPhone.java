package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.PhoneLabel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "contact_phones", uniqueConstraints = {
        @UniqueConstraint(name = "uc_contact_phone", columnNames = {"contact_id", "phone_number"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContactPhone extends BaseEntity {

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^\\+923\\d{9}$", message = "Phone number must be in normalized Pakistani format (+92...)")
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @NotNull(message = "Label is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneLabel label;

    @NotNull(message = "Contact is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Contact contact;

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLabel(PhoneLabel label) {
        this.label = label;
    }

    @Builder
    public ContactPhone(String phoneNumber, PhoneLabel label) {
        this.phoneNumber = phoneNumber;
        this.label = label;
    }
}
