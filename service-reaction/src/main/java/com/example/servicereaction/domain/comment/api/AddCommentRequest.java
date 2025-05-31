package com.example.servicereaction.domain.comment.api;

import com.example.servicereaction.domain.comment.enums.CommentTargetType;
import com.example.servicereaction.domain.comment.enums.CommentType;
import com.example.servicereaction.domain.comment.validator.AddCommentValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@AddCommentValidation
public record AddCommentRequest(
        @NotNull(message = "validation.comment.content.notNull")
        @NotBlank(message = "validation.comment.content.notBlank")
        @Size(min = 5, max = 500, message = "validation.comment.content.size")
        String content,
        @NotNull(message = "validation.comment.userId.notNull")
        @NotBlank(message = "validation.comment.userId.notBlank")
        String userId,
        @NotNull(message = "validation.comment.type.notNull")
        CommentType type,
        @NotNull(message = "validation.comment.type.notNull")
        CommentTargetType targetType,
        String targetId,
        String parentId
) {
}
