package com.revconnect.analyticsservice.controller;

import com.revconnect.analyticsservice.dto.AnalyticsSummaryDTO;
import com.revconnect.analyticsservice.dto.PostAnalyticsDTO;
import com.revconnect.analyticsservice.dto.TrackEventRequest;
import com.revconnect.analyticsservice.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/track")
    public ResponseEntity<Void> trackEvent(@Valid @RequestBody TrackEventRequest request) {
        analyticsService.trackEvent(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostAnalyticsDTO> getPostAnalytics(@PathVariable Long postId) {
        PostAnalyticsDTO analytics = analyticsService.getPostAnalytics(postId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostAnalyticsDTO>> getAllAnalytics() {
        List<PostAnalyticsDTO> analytics = analyticsService.getAllAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getAnalyticsSummary() {
        AnalyticsSummaryDTO summary = analyticsService.getAnalyticsSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<List<PostAnalyticsDTO>> getAnalyticsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getAnalyticsByUser(userId));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<AnalyticsSummaryDTO> getUserSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getUserSummary(userId));
    }
}
