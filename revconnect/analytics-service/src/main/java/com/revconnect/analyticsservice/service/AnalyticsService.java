package com.revconnect.analyticsservice.service;

import com.revconnect.analyticsservice.dto.AnalyticsSummaryDTO;
import com.revconnect.analyticsservice.dto.PostAnalyticsDTO;
import com.revconnect.analyticsservice.dto.TrackEventRequest;
import com.revconnect.analyticsservice.entity.PostAnalytics;
import com.revconnect.analyticsservice.repository.PostAnalyticsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final PostAnalyticsRepository postAnalyticsRepository;

    public AnalyticsService(PostAnalyticsRepository postAnalyticsRepository) {
        this.postAnalyticsRepository = postAnalyticsRepository;
    }

    @Transactional
    public void trackEvent(TrackEventRequest request) {
        PostAnalytics analytics = postAnalyticsRepository.findByPostId(request.getPostId())
                .orElseGet(() -> {
                    PostAnalytics a = new PostAnalytics();
                    a.setPostId(request.getPostId());
                    if (request.getAuthorId() != null) a.setAuthorId(request.getAuthorId());
                    return a;
                });

        // Backfill authorId if missing
        if (analytics.getAuthorId() == null && request.getAuthorId() != null) {
            analytics.setAuthorId(request.getAuthorId());
        }

        switch (request.getEventType().toUpperCase()) {
            case "VIEW":   analytics.setViewCount(analytics.getViewCount() + 1); break;
            case "LIKE":   analytics.setLikeCount(analytics.getLikeCount() + 1); break;
            case "UNLIKE": analytics.setLikeCount(Math.max(0, analytics.getLikeCount() - 1)); break;
            case "COMMENT":analytics.setCommentCount(analytics.getCommentCount() + 1); break;
            case "SHARE":  analytics.setShareCount(analytics.getShareCount() + 1); break;
            default: throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid event type");
        }
        postAnalyticsRepository.save(analytics);
    }

    public PostAnalyticsDTO getPostAnalytics(Long postId) {
        PostAnalytics analytics = postAnalyticsRepository.findByPostId(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Analytics not found for post"));
        return convertToDTO(analytics);
    }

    public List<PostAnalyticsDTO> getAllAnalytics() {
        return postAnalyticsRepository.findAll().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    /** All post analytics for a specific author */
    public List<PostAnalyticsDTO> getAnalyticsByUser(Long authorId) {
        return postAnalyticsRepository.findByAuthorId(authorId).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
    }

    /** Aggregated summary for a specific author */
    public AnalyticsSummaryDTO getUserSummary(Long authorId) {
        List<PostAnalytics> posts = postAnalyticsRepository.findByAuthorId(authorId);
        return buildSummary(posts);
    }

    public AnalyticsSummaryDTO getAnalyticsSummary() {
        return buildSummary(postAnalyticsRepository.findAll());
    }

    private AnalyticsSummaryDTO buildSummary(List<PostAnalytics> list) {
        long views    = list.stream().mapToLong(PostAnalytics::getViewCount).sum();
        long likes    = list.stream().mapToLong(PostAnalytics::getLikeCount).sum();
        long comments = list.stream().mapToLong(PostAnalytics::getCommentCount).sum();
        long shares   = list.stream().mapToLong(PostAnalytics::getShareCount).sum();
        long engagements = likes + comments + shares;

        AnalyticsSummaryDTO s = new AnalyticsSummaryDTO();
        s.setTotalPosts((long) list.size());
        s.setTotalViews(views);
        s.setTotalLikes(likes);
        s.setTotalComments(comments);
        s.setTotalShares(shares);
        s.setTotalEngagements(engagements);
        s.setAvgEngagementRate(views > 0 ? Math.round((engagements * 100.0 / views) * 100.0) / 100.0 : 0.0);
        return s;
    }

    private PostAnalyticsDTO convertToDTO(PostAnalytics a) {
        PostAnalyticsDTO dto = new PostAnalyticsDTO();
        dto.setId(a.getId());
        dto.setPostId(a.getPostId());
        dto.setAuthorId(a.getAuthorId());
        dto.setViewCount(a.getViewCount());
        dto.setLikeCount(a.getLikeCount());
        dto.setCommentCount(a.getCommentCount());
        dto.setShareCount(a.getShareCount());
        dto.setLastUpdated(a.getLastUpdated());
        long eng = a.getLikeCount() + a.getCommentCount() + a.getShareCount();
        dto.setEngagementRate(a.getViewCount() > 0
            ? Math.round((eng * 100.0 / a.getViewCount()) * 100.0) / 100.0 : 0.0);
        return dto;
    }
}
