package com.revconnect.interactionservice.service;

import com.revconnect.interactionservice.client.PostServiceClient;
import com.revconnect.interactionservice.client.UserServiceClient;
import com.revconnect.interactionservice.dto.CommentDTO;
import com.revconnect.interactionservice.dto.CreateCommentRequest;
import com.revconnect.interactionservice.entity.Comment;
import com.revconnect.interactionservice.repository.CommentRepository;
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
    private PostServiceClient postServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public CommentDTO createComment(CreateCommentRequest request, Long userId) {
        if (!postServiceClient.postExists(request.getPostId())) {
            throw new RuntimeException("Post not found");
        }
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUserId(userId);
        comment.setPostId(request.getPostId());
        return mapToDTO(commentRepository.save(comment));
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) throw new RuntimeException("Comment not found");
        commentRepository.deleteById(commentId);
    }

    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUserId(comment.getUserId());
        dto.setPostId(comment.getPostId());
        dto.setCreatedAt(comment.getCreatedAt());
        try {
            Map<String, Object> user = userServiceClient.getUserById(comment.getUserId());
            dto.setUsername((String) user.get("username"));
        } catch (Exception e) {
            dto.setUsername("Unknown");
        }
        return dto;
    }
}
