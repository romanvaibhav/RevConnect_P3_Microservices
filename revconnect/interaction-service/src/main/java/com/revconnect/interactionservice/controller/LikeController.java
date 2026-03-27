package com.revconnect.interactionservice.controller;

import com.revconnect.interactionservice.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<?> likePost(@RequestParam Long postId, @RequestHeader("X-User-Id") Long userId) {
        try {
            likeService.likePost(postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Post liked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> unlikePost(@RequestParam Long postId, @RequestHeader("X-User-Id") Long userId) {
        likeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of("count", likeService.getLikeCount(postId)));
    }

    @GetMapping("/post/{postId}/user")
    public ResponseEntity<Map<String, Boolean>> isLikedByUser(
            @PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of("liked", likeService.isLikedByUser(postId, userId)));
    }
}
