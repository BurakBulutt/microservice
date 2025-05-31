package com.example.servicereaction.domain.comment.mapper;

import com.example.servicereaction.domain.comment.dto.CommentDto;
import com.example.servicereaction.domain.comment.model.Comment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentServiceMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .content(comment.getContent())
                .targetType(comment.getTargetType())
                .commentType(comment.getCommentType())
                .userId(comment.getUserId())
                .targetId(comment.getTargetId())
                .build();
    }

    public static Comment toEntity(Comment comment,CommentDto commentDto,Comment parent) {
        comment.setContent(commentDto.getContent());
        comment.setCommentType(commentDto.getCommentType());
        comment.setTargetType(commentDto.getTargetType());
        comment.setParent(parent);
        comment.setUserId(commentDto.getUserId());
        comment.setTargetId(commentDto.getTargetId());

        return comment;
    }
}
