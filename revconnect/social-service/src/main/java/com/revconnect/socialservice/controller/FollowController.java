package com.revconnect.socialservice.controller;

import com.revconnect.socialservice.dto.FollowDTO;
import com.revconnect.socialservice.dto.FollowRequest;
import com.revconnect.socialservice.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<FollowDTO> followUser(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody FollowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(followService.followUser(userId, request.getFollowingId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> unfollowUser(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long followingId) {
        followService.unfollowUser(userId, followingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{followId}/accept")
    public ResponseEntity<FollowDTO> acceptFollow(@PathVariable Long followId) {
        return ResponseEntity.ok(followService.acceptFollow(followId));
    }

    @DeleteMapping("/{followId}/reject")
    public ResponseEntity<Void> rejectFollow(@PathVariable Long followId) {
        followService.rejectFollow(followId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending/received")
    public ResponseEntity<List<FollowDTO>> getPendingFollowRequests(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(followService.getPendingFollowRequests(userId));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        return ResponseEntity.ok(followService.isFollowing(followerId, followingId));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getFollowStatus(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        return ResponseEntity.ok(Map.of("status", followService.getFollowStatus(followerId, followingId)));
    }
}
