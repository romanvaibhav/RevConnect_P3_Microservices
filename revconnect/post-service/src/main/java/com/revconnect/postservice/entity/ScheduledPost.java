package com.revconnect.postservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_posts")
public class ScheduledPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, PUBLISHED, CANCELLED

    @Column(name = "published_post_id")
    private Long publishedPostId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPublishedPostId() { return publishedPostId; }
    public void setPublishedPostId(Long publishedPostId) { this.publishedPostId = publishedPostId; }
}
