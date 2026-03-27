package com.revconnect.postservice.controller;

import com.revconnect.postservice.dto.CreatePostRequest;
import com.revconnect.postservice.dto.PostDTO;
import com.revconnect.postservice.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            PostDTO post = postService.createPost(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        try {
            PostDTO post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> posts = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<?> sharePost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            PostDTO post = postService.sharePost(postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody com.revconnect.postservice.dto.UpdatePostRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            PostDTO post = postService.updatePost(postId, request, userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Not authorized")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) List<Long> followingIds,
            @RequestParam(required = false) String postType) {
        List<Long> ids = followingIds != null ? followingIds : new java.util.ArrayList<>();
        if (postType != null && !postType.isBlank()) {
            return ResponseEntity.ok(postService.getFeedFiltered(userId, ids, postType));
        }
        return ResponseEntity.ok(postService.getFeed(userId, ids));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchByKeyword(keyword));
    }

    @GetMapping("/hashtag/{tag}")
    public ResponseEntity<?> searchByHashtag(@PathVariable String tag) {
        return ResponseEntity.ok(postService.searchByHashtag(tag));
    }

    @GetMapping("/trending-hashtags")
    public ResponseEntity<?> getTrendingHashtags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(postService.getTrendingHashtags(limit));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{postId}/pin")
    public ResponseEntity<?> pinPost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        try {
            return ResponseEntity.ok(postService.pinPost(postId, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{postId}/pin")
    public ResponseEntity<?> unpinPost(@PathVariable Long postId, @RequestHeader("X-User-Id") Long userId) {
        try {
            return ResponseEntity.ok(postService.unpinPost(postId, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/scheduled")
    public ResponseEntity<?> getScheduledPosts(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(postService.getScheduledPosts(userId));
    }

    @GetMapping("/pinned/{userId}")
    public ResponseEntity<?> getPinnedPosts(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPinnedPosts(userId));
    }

    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            String ct = file.getContentType();
            if (ct == null || (!ct.startsWith("image/") && !ct.startsWith("video/"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image or video files allowed"));
            }
            String uploadDir = "/app/uploads/post-media/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String ext = "";
            String orig = file.getOriginalFilename();
            if (orig != null && orig.contains(".")) ext = orig.substring(orig.lastIndexOf("."));
            String filename = "media_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + ext;
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("mediaPath", "/uploads/post-media/" + filename));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
