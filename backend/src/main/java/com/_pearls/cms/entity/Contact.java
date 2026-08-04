package com._pearls.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "contacts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Setter(AccessLevel.PACKAGE)
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<ContactEmail> emails = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<ContactPhone> phones = new ArrayList<>();

    public List<ContactEmail> getEmails() {
        return java.util.Collections.unmodifiableList(emails);
    }

    public List<ContactPhone> getPhones() {
        return java.util.Collections.unmodifiableList(phones);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Builder
    public Contact(String firstName, String lastName, String title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
    }

    public void addEmail(ContactEmail email) {
        java.util.Objects.requireNonNull(email, "ContactEmail cannot be null");
        if (emails.contains(email)) {
            return;
        }
        Contact currentOwner = email.getContact();
        if (currentOwner == this) {
            return;
        }
        if (currentOwner != null) {
            currentOwner.removeEmail(email);
        }
        emails.add(email);
        email.setContact(this);
    }

    public void removeEmail(ContactEmail email) {
        requireNonNull(email, "ContactEmail cannot be null");
        if (emails.remove(email)) {
            email.setContact(null);
        }
    }

    public void addPhone(ContactPhone phone) {
        java.util.Objects.requireNonNull(phone, "ContactPhone cannot be null");
        if (phones.contains(phone)) {
            return;
        }
        Contact currentOwner = phone.getContact();
        if (currentOwner == this) {
            return;
        }
        if (currentOwner != null) {
            currentOwner.removePhone(phone);
        }
        phones.add(phone);
        phone.setContact(this);
    }

    public void removePhone(ContactPhone phone) {
        requireNonNull(phone, "ContactPhone cannot be null");
        if (phones.remove(phone)) {
            phone.setContact(null);
        }
    }
}
