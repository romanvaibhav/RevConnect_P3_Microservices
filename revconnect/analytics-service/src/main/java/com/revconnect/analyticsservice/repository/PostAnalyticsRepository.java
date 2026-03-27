package com.revconnect.analyticsservice.repository;

import com.revconnect.analyticsservice.entity.PostAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostAnalyticsRepository extends JpaRepository<PostAnalytics, Long> {
    Optional<PostAnalytics> findByPostId(Long postId);
    List<PostAnalytics> findByAuthorId(Long authorId);

    @Query("SELECT COALESCE(SUM(p.viewCount),0) FROM PostAnalytics p WHERE p.authorId = :authorId")
    Long sumViewsByAuthor(@Param("authorId") Long authorId);

    @Query("SELECT COALESCE(SUM(p.likeCount),0) FROM PostAnalytics p WHERE p.authorId = :authorId")
    Long sumLikesByAuthor(@Param("authorId") Long authorId);

    @Query("SELECT COALESCE(SUM(p.commentCount),0) FROM PostAnalytics p WHERE p.authorId = :authorId")
    Long sumCommentsByAuthor(@Param("authorId") Long authorId);

    @Query("SELECT COALESCE(SUM(p.shareCount),0) FROM PostAnalytics p WHERE p.authorId = :authorId")
    Long sumSharesByAuthor(@Param("authorId") Long authorId);
}
