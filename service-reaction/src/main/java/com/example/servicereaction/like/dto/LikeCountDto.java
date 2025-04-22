package com.example.servicereaction.like.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LikeCountDto {
    private String targetId;
    private Integer likeCount;
    private Integer dislikeCount;
    private Boolean isUserLiked;
    private Boolean isUserDisliked;
}
