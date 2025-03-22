package com.example.servicereaction.like.dto;

import com.example.servicereaction.like.enums.LikeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeDto {
    private String id;
    private String userId;
    private LikeType likeType;
    private String targetId;
}
