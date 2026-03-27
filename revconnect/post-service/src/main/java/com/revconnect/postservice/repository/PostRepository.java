package com.revconnect.postservice.repository;

import com.revconnect.postservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);

    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorIds AND p.postType = :postType ORDER BY p.createdAt DESC")
    List<Post> findByAuthorIdInAndPostTypeOrderByCreatedAtDesc(
        @Param("authorIds") List<Long> authorIds,
        @Param("postType") String postType);

    // Scheduled posts due for publishing
    @Query("SELECT p FROM Post p WHERE p.scheduledAt IS NOT NULL AND p.scheduledAt <= :now AND p.isPinned = false")
    List<Post> findDueScheduledPosts(@Param("now") LocalDateTime now);

    // All scheduled (future) posts for a user
    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.scheduledAt IS NOT NULL AND p.scheduledAt > :now ORDER BY p.scheduledAt ASC")
    List<Post> findScheduledByAuthor(@Param("authorId") Long authorId, @Param("now") LocalDateTime now);

    // Pinned posts for a user
    List<Post> findByAuthorIdAndIsPinnedTrueOrderByCreatedAtDesc(Long authorId);

    // Published posts for a user (excludes future scheduled posts)
    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now) ORDER BY p.createdAt DESC")
    Page<Post> findPublishedByAuthorId(@Param("authorId") Long authorId, @Param("now") LocalDateTime now, Pageable pageable);

    // Published posts for multiple authors (feed)
    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorIds AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now) ORDER BY p.createdAt DESC")
    List<Post> findPublishedByAuthorIds(@Param("authorIds") List<Long> authorIds, @Param("now") LocalDateTime now);

    // Published posts for multiple authors filtered by postType
    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorIds AND p.postType = :postType AND (p.scheduledAt IS NULL OR p.scheduledAt <= :now) ORDER BY p.createdAt DESC")
    List<Post> findPublishedByAuthorIdsAndPostType(@Param("authorIds") List<Long> authorIds, @Param("postType") String postType, @Param("now") LocalDateTime now);

}
