package com.example.servicereaction.elasticsearch;

import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import com.example.servicereaction.domain.comment.model.Comment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElasticEntityMapper {

    public static ElasticComment toElasticComment(Comment comment) {
        return ElasticComment.builder()
                .id(comment.getId())
                .created(comment.getCreated().atOffset(ZoneOffset.UTC))
                .userId(comment.getUserId())
                .targetId(comment.getTargetId())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .commentType(comment.getCommentType().name())
                .targetType(comment.getTargetType().name())
                .build();
    }
    
}
