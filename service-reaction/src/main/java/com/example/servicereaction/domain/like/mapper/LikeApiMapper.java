package com.example.servicereaction.domain.like.mapper;

import com.example.servicereaction.domain.like.api.LikeCountResponse;
import com.example.servicereaction.domain.like.api.LikeRequest;
import com.example.servicereaction.domain.like.dto.LikeCountDto;
import com.example.servicereaction.domain.like.dto.LikeDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeApiMapper {

    public static LikeCountResponse toResponse(LikeCountDto dto) {
        return LikeCountResponse.builder()
                .targetId(dto.getTargetId())
                .likeCount(dto.getLikeCount())
                .dislikeCount(dto.getDislikeCount())
                .isUserDisliked(dto.getIsUserDisliked())
                .isUserLiked(dto.getIsUserLiked())
                .build();
    }

    public static LikeDto toLikeDto(LikeRequest request) {
        return LikeDto.builder()
                .userId(request.userId())
                .targetId(request.targetId())
                .likeType(request.likeType())
                .likeTarget(request.likeTarget())
                .build();
    }
}
