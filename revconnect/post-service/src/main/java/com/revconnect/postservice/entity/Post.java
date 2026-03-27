package com.revconnect.postservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "original_post_id")
    private Long originalPostId;
    
    @Column(name = "is_shared")
    private boolean isShared = false;
    
    @Column(name = "post_type")
    private String postType = "REGULAR";
    
    @Column(name = "call_to_action")
    private String callToAction;
    
    @Column(name = "media_path")
    private String mediaPath;
    
    @Column(name = "is_pinned")
    private boolean isPinned = false;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    // JSON array of product IDs e.g. "[1,2,3]"
    @Column(name = "tagged_product_ids", columnDefinition = "TEXT")
    private String taggedProductIds;

    public Post() {
        this.createdAt = java.time.LocalDateTime.now(java.time.ZoneOffset.UTC);
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = java.time.LocalDateTime.now(java.time.ZoneOffset.UTC);
        }
        if (postType == null) {
            postType = "REGULAR";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }

    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getCallToAction() { return callToAction; }
    public void setCallToAction(String callToAction) { this.callToAction = callToAction; }

    public String getMediaPath() { return mediaPath; }
    public void setMediaPath(String mediaPath) { this.mediaPath = mediaPath; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getTaggedProductIds() { return taggedProductIds; }
    public void setTaggedProductIds(String taggedProductIds) { this.taggedProductIds = taggedProductIds; }
}
