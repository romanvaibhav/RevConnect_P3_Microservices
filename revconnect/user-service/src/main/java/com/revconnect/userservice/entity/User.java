package com.revconnect.userservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Maps to the 'users' table in MySQL
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // stored as BCrypt hash, never plain text

    @Column(nullable = false)
    private String role; // USER, CREATOR, BUSINESS

    private String bio;
    private String location;
    private String website;
    private String profilePicturePath;

    @Column(nullable = false)
    private String accountPrivacy; // PUBLIC or PRIVATE

    private LocalDateTime createdAt;

    public User() {
        this.role = "USER";
        this.accountPrivacy = "PUBLIC";
        this.createdAt = LocalDateTime.now();
    }

    // Sets default values before first save to DB
    @PrePersist
    protected void onCreate() {
        if (this.role == null || this.role.isEmpty()) this.role = "USER";
        if (this.accountPrivacy == null || this.accountPrivacy.isEmpty()) this.accountPrivacy = "PUBLIC";
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }

    public String getAccountPrivacy() { return accountPrivacy; }
    public void setAccountPrivacy(String accountPrivacy) { this.accountPrivacy = accountPrivacy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
