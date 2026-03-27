package com.revconnect.socialservice.service;

import com.revconnect.socialservice.dto.FollowDTO;
import com.revconnect.socialservice.entity.Follow;
import com.revconnect.socialservice.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock private FollowRepository followRepository;

    @InjectMocks
    private FollowService followService;

    @Test
    void followUser_shouldSaveFollow_whenNotAlreadyFollowing() {
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.empty());
        Follow saved = new Follow();
        saved.setFollowerId(1L);
        saved.setFollowingId(2L);
        when(followRepository.save(any(Follow.class))).thenReturn(saved);

        FollowDTO result = followService.followUser(1L, 2L);
        assertNotNull(result);
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    void followUser_shouldThrow_whenAlreadyFollowing() {
        Follow existing = new Follow();
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(existing));
        assertThrows(Exception.class, () -> followService.followUser(1L, 2L));
    }

    @Test
    void followUser_shouldThrow_whenFollowingSelf() {
        assertThrows(Exception.class, () -> followService.followUser(1L, 1L));
    }

    @Test
    void unfollowUser_shouldDelete_whenFollowExists() {
        Follow follow = new Follow();
        follow.setFollowerId(1L);
        follow.setFollowingId(2L);
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(follow));

        followService.unfollowUser(1L, 2L);
        verify(followRepository, times(1)).delete(follow);
    }

    @Test
    void unfollowUser_shouldThrow_whenNotFollowing() {
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> followService.unfollowUser(1L, 2L));
    }

    @Test
    void isFollowing_shouldReturnTrue_whenFollowExists() {
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.of(new Follow()));
        assertTrue(followService.isFollowing(1L, 2L));
    }

    @Test
    void isFollowing_shouldReturnFalse_whenNotFollowing() {
        when(followRepository.findByFollowerIdAndFollowingId(1L, 2L)).thenReturn(Optional.empty());
        assertFalse(followService.isFollowing(1L, 2L));
    }

    @Test
    void getFollowing_shouldReturnIds() {
        Follow f = new Follow();
        f.setFollowingId(2L);
        when(followRepository.findByFollowerId(1L)).thenReturn(List.of(f));

        List<Long> result = followService.getFollowing(1L);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0));
    }
}
