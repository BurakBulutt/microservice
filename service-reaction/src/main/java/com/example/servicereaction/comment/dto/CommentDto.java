package com.example.servicereaction.comment.dto;

import com.example.servicereaction.feign.UserResponse;
import com.example.servicereaction.comment.enums.CommentType;
import com.example.servicereaction.like.dto.LikeCountDto;
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
