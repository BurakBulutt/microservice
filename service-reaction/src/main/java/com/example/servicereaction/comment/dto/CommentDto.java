package com.example.servicereaction.comment.dto;

import com.example.servicereaction.comment.feign.UserResponse;
import com.example.servicereaction.comment.enums.CommentType;
import com.example.servicereaction.like.dto.LikeCountDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
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
