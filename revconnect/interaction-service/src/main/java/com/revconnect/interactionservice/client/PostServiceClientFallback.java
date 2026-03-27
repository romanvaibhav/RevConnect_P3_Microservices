package com.revconnect.interactionservice.client;

import org.springframework.stereotype.Component;

@Component
public class PostServiceClientFallback implements PostServiceClient {

    @Override
    public boolean postExists(Long postId) { return true; } // fail open
}
