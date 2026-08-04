package com._pearls.cms.entity;

import com._pearls.cms.entity.enums.PhoneLabel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "contact_phones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPhone extends BaseEntity {

    @NotBlank(message = "Phone Number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone format")
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull(message = "Label is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PhoneLabel label;

    @NotNull(message = "Contact is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;
}
