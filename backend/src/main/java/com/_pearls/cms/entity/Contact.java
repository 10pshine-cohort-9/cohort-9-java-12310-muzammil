package com._pearls.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @NotBlank(message = "First Name is required")
    @Size(max = 50, message = "First Name must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(max = 50, message = "Last Name must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<ContactEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<ContactPhone> phones = new ArrayList<>();

    public void addEmail(ContactEmail email) {
        emails.add(email);
        email.setContact(this);
    }

    public void removeEmail(ContactEmail email) {
        emails.remove(email);
        email.setContact(null);
    }

    public void addPhone(ContactPhone phone) {
        phones.add(phone);
        phone.setContact(this);
    }

    public void removePhone(ContactPhone phone) {
        phones.remove(phone);
        phone.setContact(null);
    }
}
