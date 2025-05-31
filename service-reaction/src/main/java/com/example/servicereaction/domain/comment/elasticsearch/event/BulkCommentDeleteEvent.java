package com.example.servicereaction.domain.comment.elasticsearch.event;

import java.util.Set;

public record BulkCommentDeleteEvent(
        Set<String> commentIds
) {
}
