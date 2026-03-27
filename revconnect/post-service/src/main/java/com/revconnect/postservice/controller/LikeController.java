package com.revconnect.postservice.controller;

import com.revconnect.postservice.service.LikeService;
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
    public ResponseEntity<?> likePost(
            @RequestParam Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            likeService.likePost(postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Post liked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> unlikePost(
            @RequestParam Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        likeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/post/{postId}/user")
    public ResponseEntity<Map<String, Boolean>> isLikedByUser(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean liked = likeService.isLikedByUser(postId, userId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
