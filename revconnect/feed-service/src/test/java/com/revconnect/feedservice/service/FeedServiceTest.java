package com.revconnect.feedservice.service;

import com.revconnect.feedservice.client.PostServiceClient;
import com.revconnect.feedservice.client.SocialServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock private PostServiceClient postServiceClient;
    @Mock private SocialServiceClient socialServiceClient;

    @InjectMocks
    private FeedService feedService;

    @Test
    void getFeed_shouldIncludeOwnPosts_andFollowingPosts() {
        when(socialServiceClient.getFollowingIds(1L)).thenReturn(List.of(2L, 3L));
        when(postServiceClient.getFeed(anyList())).thenReturn(List.of(Map.of("id", 1)));

        List<Map<String, Object>> result = feedService.getFeed(1L, null);
        assertNotNull(result);
        // authorIds passed should include userId + followingIds
        verify(postServiceClient).getFeed(argThat(ids -> ids.contains(1L) && ids.contains(2L) && ids.contains(3L)));
    }

    @Test
    void getFeed_withPostType_shouldCallFilteredEndpoint() {
        when(socialServiceClient.getFollowingIds(1L)).thenReturn(List.of(2L));
        when(postServiceClient.getFeedFiltered(anyList(), eq("PROMOTIONAL"))).thenReturn(List.of());

        feedService.getFeed(1L, "PROMOTIONAL");
        verify(postServiceClient).getFeedFiltered(anyList(), eq("PROMOTIONAL"));
        verify(postServiceClient, never()).getFeed(anyList());
    }

    @Test
    void getFeed_shouldStillWork_whenSocialServiceFails() {
        when(socialServiceClient.getFollowingIds(1L)).thenThrow(new RuntimeException("social-service down"));
        when(postServiceClient.getFeed(anyList())).thenReturn(List.of());

        // Should not throw — falls back to own posts only
        assertDoesNotThrow(() -> feedService.getFeed(1L, null));
        verify(postServiceClient).getFeed(argThat(ids -> ids.contains(1L) && ids.size() == 1));
    }

    @Test
    void searchByKeyword_shouldDelegateToPostService() {
        when(postServiceClient.searchByKeyword("java")).thenReturn(List.of(Map.of("id", 1)));
        List<Map<String, Object>> result = feedService.searchByKeyword("java");
        assertEquals(1, result.size());
    }

    @Test
    void searchByHashtag_shouldStripHashPrefix() {
        when(postServiceClient.searchByHashtag("java")).thenReturn(List.of());
        feedService.searchByHashtag("#java");
        verify(postServiceClient).searchByHashtag("java");
    }

    @Test
    void searchByHashtag_shouldWorkWithoutHashPrefix() {
        when(postServiceClient.searchByHashtag("java")).thenReturn(List.of());
        feedService.searchByHashtag("java");
        verify(postServiceClient).searchByHashtag("java");
    }

    @Test
    void getTrendingHashtags_shouldDelegateToPostService() {
        when(postServiceClient.getTrendingHashtags(10)).thenReturn(List.of(Map.of("name", "#java", "count", 5)));
        List<Map<String, Object>> result = feedService.getTrendingHashtags(10);
        assertEquals(1, result.size());
    }
}
