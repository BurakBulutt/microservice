package com.example.servicereaction.domain.comment.dto;

import com.example.servicereaction.feign.user.UserResponse;
import com.example.servicereaction.domain.comment.enums.CommentType;
import com.example.servicereaction.domain.like.dto.LikeCountDto;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentDto {
    private String id;
    private String content;
    private UserResponse user;
    private CommentDto parent;
    private List<CommentDto> commentList;
    private CommentType type;
    private String targetId;
    private LikeCountDto likeCount;
}
