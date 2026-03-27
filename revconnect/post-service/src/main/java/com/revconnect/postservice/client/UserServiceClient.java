package com.revconnect.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);

    default boolean userExists(Long userId) {
        try {
            Map<String, Object> user = getUserById(userId);
            return user != null && !user.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
