package com.revconnect.socialservice.service;

import com.revconnect.socialservice.dto.FollowDTO;
import com.revconnect.socialservice.entity.Follow;
import com.revconnect.socialservice.repository.FollowRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    // Creates a PENDING follow request
    @Transactional
    public FollowDTO followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
        }
        if (followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Follow request already sent or already following");
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        follow.setStatus("PENDING");
        return convertToDTO(followRepository.save(follow));
    }

    // Accept a pending follow request
    @Transactional
    public FollowDTO acceptFollow(Long followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow request not found"));
        if (!"PENDING".equals(follow.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is not pending");
        }
        follow.setStatus("ACCEPTED");
        return convertToDTO(followRepository.save(follow));
    }

    // Reject/delete a pending follow request
    @Transactional
    public void rejectFollow(Long followId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow request not found"));
        followRepository.delete(follow);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found"));
        followRepository.delete(follow);
    }

    // Returns only ACCEPTED followers
    public List<Long> getFollowers(Long userId) {
        return followRepository.findByFollowingIdAndStatus(userId, "ACCEPTED").stream()
                .map(Follow::getFollowerId)
                .collect(Collectors.toList());
    }

    // Returns only ACCEPTED following
    public List<Long> getFollowing(Long userId) {
        return followRepository.findByFollowerIdAndStatus(userId, "ACCEPTED").stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());
    }

    // Pending follow requests received by userId
    public List<FollowDTO> getPendingFollowRequests(Long userId) {
        return followRepository.findByFollowingIdAndStatus(userId, "PENDING").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Check if followerId is following followingId (ACCEPTED only)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .map(f -> "ACCEPTED".equals(f.getStatus()))
                .orElse(false);
    }

    // Check raw follow status (PENDING or ACCEPTED)
    public String getFollowStatus(Long followerId, Long followingId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .map(Follow::getStatus)
                .orElse("NONE");
    }

    private FollowDTO convertToDTO(Follow follow) {
        FollowDTO dto = new FollowDTO();
        dto.setId(follow.getId());
        dto.setFollowerId(follow.getFollowerId());
        dto.setFollowingId(follow.getFollowingId());
        dto.setStatus(follow.getStatus());
        dto.setCreatedAt(follow.getCreatedAt());
        return dto;
    }
}
