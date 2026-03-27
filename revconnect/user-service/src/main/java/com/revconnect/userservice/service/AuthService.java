package com.revconnect.userservice.service;

import com.revconnect.userservice.dto.AuthResponse;
import com.revconnect.userservice.dto.LoginRequest;
import com.revconnect.userservice.dto.UserDTO;
import com.revconnect.userservice.entity.User;
import com.revconnect.userservice.repository.UserRepository;
import com.revconnect.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// Handles login authentication and token validation
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Validates username + password, returns JWT token on success
    public AuthResponse authenticate(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Compare entered password with stored BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT token with userId, username, role
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthResponse(token, mapToDTO(user));
    }

    // Validates a JWT token and returns the user it belongs to
    public UserDTO validateToken(String token) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return mapToDTO(user);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    // Converts User entity to UserDTO (excludes password)
    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setWebsite(user.getWebsite());
        dto.setProfilePicturePath(user.getProfilePicturePath());
        dto.setAccountPrivacy(user.getAccountPrivacy());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
