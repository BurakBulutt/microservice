package com.example.servicereaction.domain.comment.elasticsearch.event;


import com.example.servicereaction.domain.comment.dto.CommentDto;

public record UpdateCommentEvent(
        CommentDto comment
) {
}
