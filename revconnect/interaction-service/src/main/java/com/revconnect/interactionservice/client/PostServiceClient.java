package com.revconnect.interactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", fallback = PostServiceClientFallback.class)
public interface PostServiceClient {

    @GetMapping("/api/posts/internal/exists/{postId}")
    boolean postExists(@PathVariable Long postId);
}
