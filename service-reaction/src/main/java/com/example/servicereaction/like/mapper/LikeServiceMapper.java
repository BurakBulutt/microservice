package com.example.servicereaction.like.mapper;

import com.example.servicereaction.like.dto.LikeDto;
import com.example.servicereaction.like.model.Like;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeServiceMapper {

    public static LikeDto toDto(Like like) {
        return LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .likeType(like.getLikeType())
                .targetId(like.getTargetId())
                .build();
    }

    public static Like toEntity(Like like,LikeDto dto) {
        like.setUserId(dto.getUserId());
        like.setLikeType(dto.getLikeType());
        like.setTargetId(dto.getTargetId());

        return like;
    }
}
