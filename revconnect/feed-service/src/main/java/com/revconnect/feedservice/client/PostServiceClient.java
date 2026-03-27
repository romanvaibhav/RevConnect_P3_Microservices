package com.revconnect.feedservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "post-service", fallback = PostServiceClientFallback.class)
public interface PostServiceClient {

    @GetMapping("/api/posts/internal/feed")
    List<Map<String, Object>> getFeed(@RequestParam List<Long> authorIds);

    @GetMapping("/api/posts/internal/feed/filtered")
    List<Map<String, Object>> getFeedFiltered(@RequestParam List<Long> authorIds, @RequestParam String postType);

    @GetMapping("/api/posts/internal/search")
    List<Map<String, Object>> searchByKeyword(@RequestParam String keyword);

    @GetMapping("/api/posts/internal/hashtag/{tag}")
    List<Map<String, Object>> searchByHashtag(@PathVariable String tag);

    @GetMapping("/api/posts/internal/trending-hashtags")
    List<Map<String, Object>> getTrendingHashtags(@RequestParam int limit);
}
