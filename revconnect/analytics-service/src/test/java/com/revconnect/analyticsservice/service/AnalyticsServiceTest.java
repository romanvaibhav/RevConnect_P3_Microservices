package com.revconnect.analyticsservice.service;

import com.revconnect.analyticsservice.dto.AnalyticsSummaryDTO;
import com.revconnect.analyticsservice.dto.PostAnalyticsDTO;
import com.revconnect.analyticsservice.dto.TrackEventRequest;
import com.revconnect.analyticsservice.entity.PostAnalytics;
import com.revconnect.analyticsservice.repository.PostAnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
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
class AnalyticsServiceTest {

    @Mock
    private PostAnalyticsRepository postAnalyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private PostAnalytics mockAnalytics;

    @BeforeEach
    void setUp() {
        mockAnalytics = new PostAnalytics();
        mockAnalytics.setId(1L);
        mockAnalytics.setPostId(1L);
        mockAnalytics.setAuthorId(1L);
        mockAnalytics.setViewCount(10L);
        mockAnalytics.setLikeCount(5L);
        mockAnalytics.setCommentCount(2L);
        mockAnalytics.setShareCount(1L);
    }

    @Test
    void trackEvent_VIEW_shouldIncrementViewCount() {
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));
        when(postAnalyticsRepository.save(any())).thenReturn(mockAnalytics);

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(1L);
        req.setEventType("VIEW");
        analyticsService.trackEvent(req);

        assertEquals(11L, mockAnalytics.getViewCount());
        verify(postAnalyticsRepository).save(mockAnalytics);
    }

    @Test
    void trackEvent_LIKE_shouldIncrementLikeCount() {
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));
        when(postAnalyticsRepository.save(any())).thenReturn(mockAnalytics);

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(1L);
        req.setEventType("LIKE");
        analyticsService.trackEvent(req);

        assertEquals(6L, mockAnalytics.getLikeCount());
    }

    @Test
    void trackEvent_UNLIKE_shouldDecrementLikeCount() {
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));
        when(postAnalyticsRepository.save(any())).thenReturn(mockAnalytics);

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(1L);
        req.setEventType("UNLIKE");
        analyticsService.trackEvent(req);

        assertEquals(4L, mockAnalytics.getLikeCount());
    }

    @Test
    void trackEvent_UNLIKE_shouldNotGoBelowZero() {
        mockAnalytics.setLikeCount(0L);
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));
        when(postAnalyticsRepository.save(any())).thenReturn(mockAnalytics);

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(1L);
        req.setEventType("UNLIKE");
        analyticsService.trackEvent(req);

        assertEquals(0L, mockAnalytics.getLikeCount());
    }

    @Test
    void trackEvent_invalidType_shouldThrow() {
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(1L);
        req.setEventType("INVALID");

        assertThrows(Exception.class, () -> analyticsService.trackEvent(req));
    }

    @Test
    void trackEvent_newPost_shouldCreateAnalyticsRecord() {
        when(postAnalyticsRepository.findByPostId(99L)).thenReturn(Optional.empty());
        when(postAnalyticsRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TrackEventRequest req = new TrackEventRequest();
        req.setPostId(99L);
        req.setAuthorId(1L);
        req.setEventType("VIEW");
        analyticsService.trackEvent(req);

        verify(postAnalyticsRepository).save(any(PostAnalytics.class));
    }

    @Test
    void getPostAnalytics_shouldReturnDTO_whenExists() {
        when(postAnalyticsRepository.findByPostId(1L)).thenReturn(Optional.of(mockAnalytics));
        PostAnalyticsDTO result = analyticsService.getPostAnalytics(1L);
        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals(10L, result.getViewCount());
    }

    @Test
    void getPostAnalytics_shouldThrow_whenNotFound() {
        when(postAnalyticsRepository.findByPostId(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> analyticsService.getPostAnalytics(99L));
    }

    @Test
    void getUserSummary_shouldAggregateCounts() {
        when(postAnalyticsRepository.findByAuthorId(1L)).thenReturn(List.of(mockAnalytics));
        AnalyticsSummaryDTO summary = analyticsService.getUserSummary(1L);
        assertEquals(1, summary.getTotalPosts());
        assertEquals(10, summary.getTotalViews());
        assertEquals(5, summary.getTotalLikes());
        assertEquals(2, summary.getTotalComments());
        assertEquals(1, summary.getTotalShares());
    }

    @Test
    void getAnalyticsSummary_shouldReturnGlobalSummary() {
        when(postAnalyticsRepository.findAll()).thenReturn(List.of(mockAnalytics));
        AnalyticsSummaryDTO summary = analyticsService.getAnalyticsSummary();
        assertNotNull(summary);
        assertEquals(1, summary.getTotalPosts());
    }
}
