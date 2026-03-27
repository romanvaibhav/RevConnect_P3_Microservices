package com.revconnect.feedservice.service;

import com.revconnect.feedservice.client.PostServiceClient;
import com.revconnect.feedservice.client.SocialServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FeedService {

    @Autowired
    private PostServiceClient postServiceClient;

    @Autowired
    private SocialServiceClient socialServiceClient;

    public List<Map<String, Object>> getFeed(Long userId, String postType) {
        // Get IDs of users this user follows
        List<Long> followingIds = new ArrayList<>();
        try {
            followingIds = socialServiceClient.getFollowingIds(userId);
        } catch (Exception ignored) {}

        List<Long> authorIds = new ArrayList<>(followingIds);
        authorIds.add(userId); // include own posts

        if (postType != null && !postType.isBlank()) {
            return postServiceClient.getFeedFiltered(authorIds, postType.toUpperCase());
        }
        return postServiceClient.getFeed(authorIds);
    }

    public List<Map<String, Object>> searchByKeyword(String keyword) {
        return postServiceClient.searchByKeyword(keyword);
    }

    public List<Map<String, Object>> searchByHashtag(String tag) {
        String clean = tag.startsWith("#") ? tag.substring(1) : tag;
        return postServiceClient.searchByHashtag(clean);
    }

    public List<Map<String, Object>> getTrendingHashtags(int limit) {
        return postServiceClient.getTrendingHashtags(limit);
    }
}
