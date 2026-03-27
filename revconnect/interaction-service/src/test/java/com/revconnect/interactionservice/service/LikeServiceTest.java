package com.revconnect.interactionservice.service;

import com.revconnect.interactionservice.client.PostServiceClient;
import com.revconnect.interactionservice.entity.Like;
import com.revconnect.interactionservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private PostServiceClient postServiceClient;

    @InjectMocks
    private LikeService likeService;

    @Test
    void likePost_shouldSaveLike_whenPostExistsAndNotAlreadyLiked() {
        when(postServiceClient.postExists(1L)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(1L, 1L)).thenReturn(false);

        likeService.likePost(1L, 1L);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likePost_shouldNotSaveDuplicate_whenAlreadyLiked() {
        when(postServiceClient.postExists(1L)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(1L, 1L)).thenReturn(true);

        likeService.likePost(1L, 1L);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void likePost_shouldThrow_whenPostNotFound() {
        when(postServiceClient.postExists(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> likeService.likePost(99L, 1L));
    }

    @Test
    void unlikePost_shouldCallDelete() {
        likeService.unlikePost(1L, 1L);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(1L, 1L);
    }

    @Test
    void isLikedByUser_shouldReturnTrue_whenLikeExists() {
        when(likeRepository.existsByPostIdAndUserId(1L, 1L)).thenReturn(true);
        assertTrue(likeService.isLikedByUser(1L, 1L));
    }

    @Test
    void getLikeCount_shouldReturnCount() {
        when(likeRepository.countByPostId(1L)).thenReturn(5L);
        assertEquals(5L, likeService.getLikeCount(1L));
    }
}
