package com.revconnect.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/service")
    public ResponseEntity<?> genericFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/post-service")
    public ResponseEntity<?> postServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Post service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/product-service")
    public ResponseEntity<?> productServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Product service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/social-service")
    public ResponseEntity<?> socialServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Social service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/analytics-service")
    public ResponseEntity<?> analyticsServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Analytics service is currently unavailable. Please try again later."));
    }

    @RequestMapping("/notification-service")
    public ResponseEntity<?> notificationServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Notification service is currently unavailable. Please try again later."));
    }
}
