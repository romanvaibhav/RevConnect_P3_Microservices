package com.revconnect.interactionservice.controller;

import com.revconnect.interactionservice.dto.CommentDTO;
import com.revconnect.interactionservice.dto.CreateCommentRequest;
import com.revconnect.interactionservice.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CreateCommentRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            CommentDTO comment = commentService.createComment(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Map<String, Long>> getCommentCount(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of("count", commentService.getCommentCount(postId)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
