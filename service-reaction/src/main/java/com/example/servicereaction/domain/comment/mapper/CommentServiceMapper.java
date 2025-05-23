package com.example.servicereaction.domain.comment.mapper;

import com.example.servicereaction.feign.user.UserResponse;
import com.example.servicereaction.domain.comment.dto.CommentDto;
import com.example.servicereaction.domain.comment.model.Comment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentServiceMapper {
    public static CommentDto toDto(Comment comment, UserResponse user) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .type(comment.getType())
                .targetId(comment.getTargetId())
                .user(user)
                .build();
    }

    public static Comment toEntity(Comment comment,CommentDto commentDto,Comment parent) {
        comment.setContent(commentDto.getContent());
        comment.setUserId(commentDto.getUser().getId());
        comment.setType(commentDto.getType());
        comment.setParent(parent);
        comment.setTargetId(commentDto.getTargetId());
        return comment;
    }
}
