package com.revconnect.socialservice.dto;

import jakarta.validation.constraints.NotNull;

public class ConnectionRequest {
    
    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
