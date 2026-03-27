package com.revconnect.analyticsservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrackEventRequest {
    
    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotBlank(message = "Event type is required")
    private String eventType;

    private Long authorId; // optional — used to associate analytics with post owner

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
