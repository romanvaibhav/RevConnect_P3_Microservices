package com.revconnect.interactionservice.service;

import com.revconnect.interactionservice.client.PostServiceClient;
import com.revconnect.interactionservice.entity.Like;
import com.revconnect.interactionservice.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostServiceClient postServiceClient;

    @Transactional
    public void likePost(Long postId, Long userId) {
        if (!postServiceClient.postExists(postId)) {
            throw new RuntimeException("Post not found");
        }
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) return;
        Like like = new Like();
        like.setPostId(postId);
        like.setUserId(userId);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
