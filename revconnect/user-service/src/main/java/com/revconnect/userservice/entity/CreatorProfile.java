package com.revconnect.userservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "creator_profiles")
public class CreatorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Basic creator identity
    private String creatorName;
    private String contentCategory;

    @Column(columnDefinition = "TEXT")
    private String detailedBio;

    // Contact
    private String contactEmail;
    private String contactPhone;

    // Links
    private String websiteUrl;
    private String portfolioUrl;

    // JSON arrays stored as TEXT: ["url1","url2"]
    @Column(columnDefinition = "TEXT")
    private String socialLinks;   // e.g. [{"platform":"YouTube","url":"..."}]

    @Column(columnDefinition = "TEXT")
    private String externalLinks; // endorsements/partnerships [{"label":"...","url":"..."}]

    private Integer subscriberCount;

    // ── Getters & Setters ──

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public String getContentCategory() { return contentCategory; }
    public void setContentCategory(String contentCategory) { this.contentCategory = contentCategory; }

    public String getDetailedBio() { return detailedBio; }
    public void setDetailedBio(String detailedBio) { this.detailedBio = detailedBio; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }

    public String getExternalLinks() { return externalLinks; }
    public void setExternalLinks(String externalLinks) { this.externalLinks = externalLinks; }

    public Integer getSubscriberCount() { return subscriberCount; }
    public void setSubscriberCount(Integer subscriberCount) { this.subscriberCount = subscriberCount; }
}
