package com.revconnect.postservice.service;

import com.revconnect.postservice.client.UserServiceClient;
import com.revconnect.postservice.dto.CreatePostRequest;
import com.revconnect.postservice.dto.PostDTO;
import com.revconnect.postservice.entity.*;
import com.revconnect.postservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private PostHashtagRepository postHashtagRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\w+)");

    @Transactional
    public PostDTO createPost(CreatePostRequest request, Long userId) {
        // Validate user exists
        try {
            userServiceClient.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("User not found");
        }

        // Validate post type
        if (request.getPostType() != null &&
            !request.getPostType().matches("^(REGULAR|PROMOTIONAL|ANNOUNCEMENT)$")) {
            throw new IllegalArgumentException("Invalid post type");
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setAuthorId(userId);
        post.setPostType(request.getPostType() != null ? request.getPostType() : "REGULAR");
        post.setCallToAction(request.getCallToAction());
        post.setMediaPath(request.getMediaPath());

        // Handle scheduling
        if (request.getScheduledAt() != null && !request.getScheduledAt().isBlank()) {
            LocalDateTime scheduledAt = LocalDateTime.parse(
                request.getScheduledAt(), DateTimeFormatter.ISO_DATE_TIME);
            post.setScheduledAt(scheduledAt);
        }

        // Handle tagged products (store as JSON array string)
        if (request.getTaggedProductIds() != null && !request.getTaggedProductIds().isEmpty()) {
            post.setTaggedProductIds(request.getTaggedProductIds().toString());
        }

        Post savedPost = postRepository.save(post);

        // Process hashtags
        List<String> hashtags = extractHashtags(request.getContent());
        if (request.getHashtags() != null) {
            hashtags.addAll(request.getHashtags());
        }
        processHashtags(savedPost.getId(), hashtags);

        return mapToDTO(savedPost);
    }

    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToDTO(post);
    }

    public Page<PostDTO> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findPublishedByAuthorId(userId, LocalDateTime.now(java.time.ZoneOffset.UTC), pageable);
        return posts.map(this::mapToDTO);
    }

    @Transactional
    public PostDTO sharePost(Long postId, Long userId) {
        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post sharedPost = new Post();
        sharedPost.setContent(originalPost.getContent());
        sharedPost.setAuthorId(userId);
        sharedPost.setOriginalPostId(postId);
        sharedPost.setShared(true);
        sharedPost.setPostType(originalPost.getPostType());

        Post savedPost = postRepository.save(sharedPost);
        return mapToDTO(savedPost);
    }

    @Transactional
    public PostDTO updatePost(Long postId, com.revconnect.postservice.dto.UpdatePostRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Not authorized to edit this post");
        }
        post.setContent(request.getContent());
        Post saved = postRepository.save(post);

        // Re-process hashtags: remove old, add new
        postHashtagRepository.deleteByPostId(postId);
        List<String> hashtags = extractHashtags(request.getContent());
        if (request.getHashtags() != null) hashtags.addAll(request.getHashtags());
        processHashtags(saved.getId(), hashtags);

        return mapToDTO(saved);
    }

    @Transactional
    public void deletePost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(postId);
    }

    // Personalized feed: posts from followed users + own posts
    public List<PostDTO> getFeed(Long userId, List<Long> followingIds) {
        List<Long> authorIds = new ArrayList<>(followingIds);
        authorIds.add(userId); // include own posts
        List<Post> posts = postRepository.findPublishedByAuthorIds(authorIds, LocalDateTime.now(java.time.ZoneOffset.UTC));
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Personalized feed filtered by post type
    public List<PostDTO> getFeedFiltered(Long userId, List<Long> followingIds, String postType) {
        List<Long> authorIds = new ArrayList<>(followingIds);
        authorIds.add(userId);
        List<Post> posts = postRepository.findPublishedByAuthorIdsAndPostType(authorIds, postType.toUpperCase(), LocalDateTime.now(java.time.ZoneOffset.UTC));
        return posts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Internal: feed by explicit author IDs (called by feed-service via Feign)
    public List<PostDTO> getFeedByAuthorIds(List<Long> authorIds) {
        return postRepository.findPublishedByAuthorIds(authorIds, LocalDateTime.now(java.time.ZoneOffset.UTC))
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Internal: filtered feed by explicit author IDs (called by feed-service via Feign)
    public List<PostDTO> getFeedFilteredByAuthorIds(List<Long> authorIds, String postType) {
        return postRepository.findPublishedByAuthorIdsAndPostType(authorIds, postType.toUpperCase(), LocalDateTime.now(java.time.ZoneOffset.UTC))
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<PostDTO> searchByKeyword(String keyword) {
        return postRepository.searchByKeyword(keyword).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    // Search posts by hashtag name
    public List<PostDTO> searchByHashtag(String tag) {
        String normalizedTag = tag.startsWith("#") ? tag : "#" + tag;
        return hashtagRepository.findByNameIgnoreCase(normalizedTag).map(hashtag -> {
            List<PostHashtag> postHashtags = postHashtagRepository.findByHashtagId(hashtag.getId());
            return postHashtags.stream()
                    .map(ph -> postRepository.findById(ph.getPostId()))
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }).orElse(new ArrayList<>());
    }

    // Pin a post
    @Transactional
    public PostDTO pinPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthorId().equals(userId)) throw new RuntimeException("Not authorized");
        post.setPinned(true);
        return mapToDTO(postRepository.save(post));
    }

    // Unpin a post
    @Transactional
    public PostDTO unpinPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getAuthorId().equals(userId)) throw new RuntimeException("Not authorized");
        post.setPinned(false);
        return mapToDTO(postRepository.save(post));
    }

    // Get scheduled (future) posts for a user
    public List<PostDTO> getScheduledPosts(Long userId) {
        return postRepository.findScheduledByAuthor(userId, LocalDateTime.now(java.time.ZoneOffset.UTC))
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get pinned posts for a user
    public List<PostDTO> getPinnedPosts(Long userId) {
        return postRepository.findByAuthorIdAndIsPinnedTrueOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Publish scheduled posts every minute
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void publishScheduledPosts() {
        List<Post> due = postRepository.findDueScheduledPosts(LocalDateTime.now(java.time.ZoneOffset.UTC));
        for (Post post : due) {
            post.setScheduledAt(null); // clear schedule — now it's live
            postRepository.save(post);
        }
    }

    // Trending hashtags (top N by usage count)
    public List<java.util.Map<String, Object>> getTrendingHashtags(int limit) {        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        return hashtagRepository.findTopByOrderByUsageCountDesc(pageable).stream()
                .map(h -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("name", h.getName());
                    m.put("count", h.getUsageCount());
                    return m;
                }).collect(Collectors.toList());
    }

    private List<String> extractHashtags(String content) {
        List<String> hashtags = new ArrayList<>();
        if (content == null) return hashtags;

        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            hashtags.add("#" + matcher.group(1));
        }
        return hashtags;
    }

    private void processHashtags(Long postId, List<String> hashtagNames) {
        for (String name : hashtagNames) {
            Hashtag hashtag = hashtagRepository.findByName(name)
                    .orElseGet(() -> {
                        Hashtag newHashtag = new Hashtag();
                        newHashtag.setName(name);
                        newHashtag.setUsageCount(0);
                        return hashtagRepository.save(newHashtag);
                    });

            // Increment usage count
            hashtag.setUsageCount(hashtag.getUsageCount() + 1);
            hashtagRepository.save(hashtag);

            // Link post to hashtag
            PostHashtag postHashtag = new PostHashtag();
            postHashtag.setPostId(postId);
            postHashtag.setHashtagId(hashtag.getId());
            postHashtagRepository.save(postHashtag);
        }
    }

    private PostDTO mapToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthorId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setOriginalPostId(post.getOriginalPostId());
        dto.setShared(post.isShared());
        dto.setPostType(post.getPostType());
        dto.setCallToAction(post.getCallToAction());
        dto.setMediaPath(post.getMediaPath());
        dto.setPinned(post.isPinned());
        dto.setScheduledAt(post.getScheduledAt());
        dto.setPublished(post.getScheduledAt() == null || post.getScheduledAt().isBefore(LocalDateTime.now(java.time.ZoneOffset.UTC)));

        // Parse tagged product IDs from JSON string
        if (post.getTaggedProductIds() != null && !post.getTaggedProductIds().isBlank()) {
            try {
                String raw = post.getTaggedProductIds().replaceAll("[\\[\\]\\s]", "");
                if (!raw.isEmpty()) {
                    List<Long> ids = java.util.Arrays.stream(raw.split(","))
                            .map(Long::parseLong).collect(Collectors.toList());
                    dto.setTaggedProductIds(ids);
                }
            } catch (Exception ignored) {}
        }

        // Get author name
        try {
            Map<String, Object> user = userServiceClient.getUserById(post.getAuthorId());
            dto.setAuthorName((String) user.get("username"));
        } catch (Exception e) {
            dto.setAuthorName("Unknown");
        }

        // For shared posts, fetch original author info
        if (post.isShared() && post.getOriginalPostId() != null) {
            postRepository.findById(post.getOriginalPostId()).ifPresent(original -> {
                dto.setOriginalAuthorId(original.getAuthorId());
                try {
                    Map<String, Object> origUser = userServiceClient.getUserById(original.getAuthorId());
                    dto.setOriginalAuthorName((String) origUser.get("username"));
                } catch (Exception e) {
                    dto.setOriginalAuthorName("Unknown");
                }
            });
        }

        // Get hashtags
        List<PostHashtag> postHashtags = postHashtagRepository.findByPostId(post.getId());
        List<String> hashtags = postHashtags.stream()
                .map(ph -> hashtagRepository.findById(ph.getHashtagId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getName())
                .collect(Collectors.toList());
        dto.setHashtags(hashtags);

        // Counts are fetched by the frontend directly from interaction-service on load.
        // Setting 0 here avoids N+1 Feign calls that would slow down feed loading.
        dto.setLikeCount(0L);
        dto.setCommentCount(0L);

        return dto;
    }
}
