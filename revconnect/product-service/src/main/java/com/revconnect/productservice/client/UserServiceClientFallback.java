package com.revconnect.productservice.client;

import com.revconnect.productservice.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserDTO getUserById(Long userId) {
        UserDTO fallback = new UserDTO();
        fallback.setId(userId);
        fallback.setUsername("Unknown");
        return fallback;
    }
}
