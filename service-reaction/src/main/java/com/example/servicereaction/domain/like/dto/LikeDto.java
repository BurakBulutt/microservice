package com.example.servicereaction.domain.like.dto;

import com.example.servicereaction.domain.like.enums.LikeTarget;
import com.example.servicereaction.domain.like.enums.LikeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeDto {
    private String id;
    private String userId;
    private LikeType likeType;
    private LikeTarget likeTarget;
    private String targetId;
}
