package com.example.servicereaction.domain.comment.repo;

import com.example.servicereaction.domain.comment.model.Comment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentSpec {

    public static Specification<Comment> byTargetId(String targetId) {
        return (root, query, cb) -> {
            if (targetId == null || targetId.isBlank()) return null;
            return cb.equal(root.get("targetId"), targetId);
        };
    }
}
