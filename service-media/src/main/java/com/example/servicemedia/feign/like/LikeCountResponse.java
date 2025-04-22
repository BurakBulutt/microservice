package com.example.servicemedia.feign.like;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LikeCountResponse {
    private String targetId;
    private Integer likeCount;
    private Integer dislikeCount;
    private Boolean isUserLiked;
    private Boolean isUserDisliked;
}
