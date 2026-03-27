package com.revconnect.socialservice.dto;

import jakarta.validation.constraints.NotNull;

public class FollowRequest {
    
    @NotNull(message = "Following ID is required")
    private Long followingId;

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}
