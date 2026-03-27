package com.revconnect.postservice.controller;

import com.revconnect.postservice.dto.PostDTO;
import com.revconnect.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Internal endpoints for service-to-service communication only.
 * Called by feed-service and interaction-service via Feign.
 */
@RestController
@RequestMapping("/api/posts/internal")
public class PostInternalController {

    @Autowired
    private PostService postService;

    @GetMapping("/exists/{postId}")
    public ResponseEntity<Boolean> postExists(@PathVariable Long postId) {
        try {
            postService.getPostById(postId);
            return ResponseEntity.ok(true);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getFeed(@RequestParam List<Long> authorIds) {
        return ResponseEntity.ok(postService.getFeedByAuthorIds(authorIds));
    }

    @GetMapping("/feed/filtered")
    public ResponseEntity<List<PostDTO>> getFeedFiltered(
            @RequestParam List<Long> authorIds,
            @RequestParam String postType) {
        return ResponseEntity.ok(postService.getFeedFilteredByAuthorIds(authorIds, postType));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchByKeyword(keyword));
    }

    @GetMapping("/hashtag/{tag}")
    public ResponseEntity<List<PostDTO>> searchByHashtag(@PathVariable String tag) {
        return ResponseEntity.ok(postService.searchByHashtag(tag));
    }

    @GetMapping("/trending-hashtags")
    public ResponseEntity<List<Map<String, Object>>> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getTrendingHashtags(limit));
    }
}
