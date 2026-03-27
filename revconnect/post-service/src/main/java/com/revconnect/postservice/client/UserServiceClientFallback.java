package com.revconnect.postservice.client;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public Map<String, Object> getUserById(Long userId) {
        return Map.of("id", userId, "username", "Unknown");
    }
}
