package com.example.servicereaction.domain.like.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeCountResponse {
    private String targetId;
    private Integer likeCount;
    private Integer dislikeCount;
    private Boolean isUserLiked;
    private Boolean isUserDisliked;
}
