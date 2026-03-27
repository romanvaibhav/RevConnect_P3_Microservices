package com.revconnect.analyticsservice.dto;

public class AnalyticsSummaryDTO {
    private Long totalPosts;
    private Long totalViews;
    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private Double avgEngagementRate;
    private Long totalEngagements; // likes + comments + shares

    public Long getTotalPosts() { return totalPosts; }
    public void setTotalPosts(Long totalPosts) { this.totalPosts = totalPosts; }

    public Long getTotalViews() { return totalViews; }
    public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }

    public Long getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Long totalLikes) { this.totalLikes = totalLikes; }

    public Long getTotalComments() { return totalComments; }
    public void setTotalComments(Long totalComments) { this.totalComments = totalComments; }

    public Long getTotalShares() { return totalShares; }
    public void setTotalShares(Long totalShares) { this.totalShares = totalShares; }

    public Double getAvgEngagementRate() { return avgEngagementRate; }
    public void setAvgEngagementRate(Double avgEngagementRate) { this.avgEngagementRate = avgEngagementRate; }

    public Long getTotalEngagements() { return totalEngagements; }
    public void setTotalEngagements(Long totalEngagements) { this.totalEngagements = totalEngagements; }
}
