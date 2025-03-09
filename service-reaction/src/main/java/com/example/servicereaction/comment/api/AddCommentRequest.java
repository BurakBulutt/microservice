package com.example.servicereaction.comment.api;

import com.example.servicereaction.comment.enums.CommentType;

public record AddCommentRequest(
    String content,
    String userId,
    CommentType type,
    String targetId,
    String parentId
) {
}
