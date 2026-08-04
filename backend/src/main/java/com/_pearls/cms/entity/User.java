package com._pearls.cms.entity;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Email(message = "Invalid email format")
    @Column(unique = true, length = 255)
    private String email;

    @Pattern(regexp = "^\\+923\\d{9}$", message = "Phone number must be in normalized Pakistani format (+92...)")
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Contact> contacts = new ArrayList<>();

    public List<Contact> getContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Builder
    public User(String email, String phoneNumber, String password) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public void addContact(Contact contact) {
        Objects.requireNonNull(contact, "Contact cannot be null");
        if (contacts.contains(contact)) {
            return;
        }
        User currentOwner = contact.getUser();
        if (currentOwner == this) {
            return;
        }
        if (currentOwner != null) {
            currentOwner.removeContact(contact);
        }
        contacts.add(contact);
        contact.setUser(this);
    }

    public void removeContact(Contact contact) {
        Objects.requireNonNull(contact, "Contact cannot be null");
        if (contacts.remove(contact)) {
            contact.setUser(null);
        }
    }
}
