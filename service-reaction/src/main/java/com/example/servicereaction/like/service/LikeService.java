package com.example.servicereaction.like.service;

import com.example.servicereaction.like.dto.LikeCountDto;
import com.example.servicereaction.like.dto.LikeDto;

public interface LikeService {
    LikeCountDto findLikeCount(String targetId,String userId);
    void save(LikeDto likeDto);
    void delete(String id);
}
