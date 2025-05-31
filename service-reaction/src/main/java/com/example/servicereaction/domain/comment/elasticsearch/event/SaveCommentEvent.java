package com.example.servicereaction.domain.comment.elasticsearch.event;

import com.example.servicereaction.domain.comment.model.Comment;

public record SaveCommentEvent(
        Comment comment
) {
}
