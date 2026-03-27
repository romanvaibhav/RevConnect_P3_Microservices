package com.revconnect.postservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private Long originalPostId;
    @JsonProperty("isShared")
    private boolean isShared;
    private Long originalAuthorId;
    private String originalAuthorName;
    private String postType;
    private String callToAction;
    private String mediaPath;
    @JsonProperty("isPinned")
    private boolean isPinned;
    private List<String> hashtags;
    private long likeCount;
    private long commentCount;
    private LocalDateTime scheduledAt;
    private List<Long> taggedProductIds;
    @JsonProperty("isPublished")
    private boolean isPublished; // false when scheduledAt is in the future

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }

    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }

    public Long getOriginalAuthorId() { return originalAuthorId; }
    public void setOriginalAuthorId(Long originalAuthorId) { this.originalAuthorId = originalAuthorId; }

    public String getOriginalAuthorName() { return originalAuthorName; }
    public void setOriginalAuthorName(String originalAuthorName) { this.originalAuthorName = originalAuthorName; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getCallToAction() { return callToAction; }
    public void setCallToAction(String callToAction) { this.callToAction = callToAction; }

    public String getMediaPath() { return mediaPath; }
    public void setMediaPath(String mediaPath) { this.mediaPath = mediaPath; }

    public boolean isPinned() { return isPinned; }
    public void setPinned(boolean pinned) { isPinned = pinned; }

    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public long getCommentCount() { return commentCount; }
    public void setCommentCount(long commentCount) { this.commentCount = commentCount; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public List<Long> getTaggedProductIds() { return taggedProductIds; }
    public void setTaggedProductIds(List<Long> taggedProductIds) { this.taggedProductIds = taggedProductIds; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
}
