package com.revconnect.feedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "social-service", fallback = SocialServiceClientFallback.class)
public interface SocialServiceClient {

    @GetMapping("/api/follows/following/{userId}")
    List<Long> getFollowingIds(@PathVariable Long userId);
}
