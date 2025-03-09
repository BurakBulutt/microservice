package com.example.servicereaction.like.api;

import com.example.servicereaction.like.enums.LikeType;

public record LikeRequest(
    String userId,
    String targetId,
    LikeType likeType
) {
}
