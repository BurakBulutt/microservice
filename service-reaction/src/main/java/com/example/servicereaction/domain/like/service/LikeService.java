package com.example.servicereaction.domain.like.service;

import com.example.servicereaction.domain.like.dto.LikeCountDto;
import com.example.servicereaction.domain.like.dto.LikeDto;
import com.example.servicereaction.domain.like.enums.LikeType;

import java.util.Set;

public interface LikeService {
    LikeCountDto findLikeCount(String targetId);

    String getTopContentLikeTarget(LikeType likeType);

    void save(LikeDto likeDto);

    void delete(String id);
    void deleteLikesByTargetId(String targetId);
    void deleteLikesByTargetIdIn(Set<String> targetIds);
    void deleteUserLikes(String userId);
}
