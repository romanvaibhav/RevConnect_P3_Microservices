package com.revconnect.feedservice.controller;

import com.revconnect.feedservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    // GET /api/feed?postType=REGULAR — personalized feed for logged-in user
    @GetMapping
    public ResponseEntity<?> getFeed(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String postType) {
        return ResponseEntity.ok(feedService.getFeed(userId, postType));
    }

    // GET /api/feed/search?keyword=
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(feedService.searchByKeyword(keyword));
    }

    // GET /api/feed/hashtag/{tag}
    @GetMapping("/hashtag/{tag}")
    public ResponseEntity<?> searchByHashtag(@PathVariable String tag) {
        return ResponseEntity.ok(feedService.searchByHashtag(tag));
    }

    // GET /api/feed/trending-hashtags?limit=10
    @GetMapping("/trending-hashtags")
    public ResponseEntity<?> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(feedService.getTrendingHashtags(limit));
    }
}
