package com.revconnect.feedservice.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PostServiceClientFallback implements PostServiceClient {

    @Override
    public List<Map<String, Object>> getFeed(List<Long> authorIds) { return Collections.emptyList(); }

    @Override
    public List<Map<String, Object>> getFeedFiltered(List<Long> authorIds, String postType) { return Collections.emptyList(); }

    @Override
    public List<Map<String, Object>> searchByKeyword(String keyword) { return Collections.emptyList(); }

    @Override
    public List<Map<String, Object>> searchByHashtag(String tag) { return Collections.emptyList(); }

    @Override
    public List<Map<String, Object>> getTrendingHashtags(int limit) { return Collections.emptyList(); }
}
