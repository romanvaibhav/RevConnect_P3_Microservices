package com.revconnect.postservice.service;

import com.revconnect.postservice.client.UserServiceClient;
import com.revconnect.postservice.dto.CommentDTO;
import com.revconnect.postservice.dto.CreateCommentRequest;
import com.revconnect.postservice.entity.Comment;
import com.revconnect.postservice.repository.CommentRepository;
import com.revconnect.postservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public CommentDTO createComment(CreateCommentRequest request, Long userId) {
        // Verify post exists
        if (!postRepository.existsById(request.getPostId())) {
            throw new RuntimeException("Post not found");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setPostId(request.getPostId());

        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setPostId(comment.getPostId());
        dto.setCreatedAt(comment.getCreatedAt());

        // Get username
        try {
            Map<String, Object> user = userServiceClient.getUserById(comment.getUserId());
            dto.setUsername((String) user.get("username"));
        } catch (Exception e) {
            dto.setUsername("Unknown");
        }

        return dto;
    }
}
