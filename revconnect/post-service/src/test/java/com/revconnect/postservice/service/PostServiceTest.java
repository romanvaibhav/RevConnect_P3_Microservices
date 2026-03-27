package com.revconnect.postservice.service;

import com.revconnect.postservice.client.UserServiceClient;
import com.revconnect.postservice.dto.CreatePostRequest;
import com.revconnect.postservice.dto.PostDTO;
import com.revconnect.postservice.entity.Post;
import com.revconnect.postservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private HashtagRepository hashtagRepository;
    @Mock private PostHashtagRepository postHashtagRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks
    private PostService postService;

    private Post mockPost;

    @BeforeEach
    void setUp() {
        mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setContent("Test post content");
        mockPost.setAuthorId(1L);
        mockPost.setPostType("REGULAR");
    }

    @Test
    void getPostById_shouldReturnPost_whenExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        when(userServiceClient.getUserById(anyLong())).thenReturn(Map.of("username", "testuser"));
        when(postHashtagRepository.findByPostId(anyLong())).thenReturn(List.of());
        when(likeRepository.countByPostId(anyLong())).thenReturn(0L);
        when(commentRepository.countByPostId(anyLong())).thenReturn(0L);

        PostDTO result = postService.getPostById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test post content", result.getContent());
    }

    @Test
    void getPostById_shouldThrow_whenNotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.getPostById(99L));
    }

    @Test
    void deletePost_shouldDelete_whenExists() {
        when(postRepository.existsById(1L)).thenReturn(true);
        postService.deletePost(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletePost_shouldThrow_whenNotFound() {
        when(postRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> postService.deletePost(99L));
    }

    @Test
    void createPost_shouldThrow_whenUserNotFound() {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("Hello");
        request.setPostType("REGULAR");

        when(userServiceClient.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));
        assertThrows(RuntimeException.class, () -> postService.createPost(request, 1L));
        verify(postRepository, never()).save(any());
    }

    @Test
    void pinPost_shouldThrow_whenNotAuthor() {
        mockPost.setAuthorId(2L); // different user
        when(postRepository.findById(1L)).thenReturn(Optional.of(mockPost));
        assertThrows(RuntimeException.class, () -> postService.pinPost(1L, 1L));
    }
}
