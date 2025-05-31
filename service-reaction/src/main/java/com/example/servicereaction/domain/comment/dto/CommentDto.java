package com.example.servicereaction.domain.comment.dto;

import com.example.servicereaction.domain.comment.enums.CommentTargetType;
import com.example.servicereaction.feign.TargetResponse;
import com.example.servicereaction.feign.user.UserResponse;
import com.example.servicereaction.domain.comment.enums.CommentType;
import com.example.servicereaction.domain.like.dto.LikeCountDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentDto {
    private String id;
    private LocalDateTime created;
    private String content;
    private CommentDto parent;
    private List<CommentDto> commentList;
    private CommentType commentType;
    private CommentTargetType targetType;
    private LikeCountDto likeCount;
    private TargetResponse target;
    private UserResponse user;
    private String targetId;
    private String userId;
}
