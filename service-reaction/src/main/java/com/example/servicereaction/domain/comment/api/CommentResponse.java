package com.example.servicereaction.domain.comment.api;

import com.example.servicereaction.domain.comment.dto.CommentDto;
import com.example.servicereaction.feign.TargetResponse;
import com.example.servicereaction.domain.comment.enums.CommentTargetType;
import com.example.servicereaction.domain.comment.enums.CommentType;
import com.example.servicereaction.feign.user.UserResponse;
import com.example.servicereaction.domain.like.dto.LikeCountDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentResponse {
    private String id;
    private String content;
    private UserResponse user;
    private CommentDto parent;
    private List<CommentDto> commentList;
    private CommentType commentType;
    private CommentTargetType targetType;
    private LikeCountDto likeCount;
    private TargetResponse target;
}
