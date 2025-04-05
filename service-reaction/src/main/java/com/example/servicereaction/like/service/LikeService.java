package com.example.servicereaction.like.service;

import com.example.servicereaction.like.dto.LikeCountDto;
import com.example.servicereaction.like.dto.LikeDto;

import java.util.Set;

public interface LikeService {
    LikeCountDto findLikeCount(String targetId);
    void save(LikeDto likeDto);

    void delete(String id);
    void deleteLikesByTargetId(String targetId);
    void deleteLikesByTargetIdIn(Set<String> targetIds);
    void deleteUserLikes(String userId);
}
