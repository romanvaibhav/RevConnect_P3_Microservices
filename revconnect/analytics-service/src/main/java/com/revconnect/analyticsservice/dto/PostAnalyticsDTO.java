package com.revconnect.analyticsservice.dto;

import java.time.LocalDateTime;

public class PostAnalyticsDTO {
    private Long id;
    private Long postId;
    private Long authorId;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;
    private LocalDateTime lastUpdated;
    // Derived: (likes + comments + shares) / views * 100
    private Double engagementRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }

    public Long getShareCount() { return shareCount; }
    public void setShareCount(Long shareCount) { this.shareCount = shareCount; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public Double getEngagementRate() { return engagementRate; }
    public void setEngagementRate(Double engagementRate) { this.engagementRate = engagementRate; }
}
