package com.revconnect.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "business_profiles")
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Core identity
    private String companyName;
    private String industry;
    private String companySize;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Contact
    private String contactEmail;
    private String phoneNumber;

    // Address (business-only)
    private String businessAddress;
    private String city;
    private String country;

    // Links
    private String websiteUrl;

    // JSON stored as TEXT
    @Column(columnDefinition = "TEXT")
    private String socialLinks;   // [{"platform":"LinkedIn","url":"..."}]

    @Column(columnDefinition = "TEXT")
    private String businessHours; // {"Mon":"9am-5pm","Tue":"9am-5pm",...}

    @Column(columnDefinition = "TEXT")
    private String externalLinks; // endorsements/partnerships [{"label":"...","url":"..."}]

    // ── Getters & Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getCompanySize() { return companySize; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getExternalLinks() { return externalLinks; }
    public void setExternalLinks(String externalLinks) { this.externalLinks = externalLinks; }
}
