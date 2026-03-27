package com.revconnect.feedservice.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SocialServiceClientFallback implements SocialServiceClient {

    @Override
    public List<Long> getFollowingIds(Long userId) { return Collections.emptyList(); }
}
