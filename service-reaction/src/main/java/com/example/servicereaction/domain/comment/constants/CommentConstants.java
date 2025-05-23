package com.example.servicereaction.domain.comment.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentConstants {
    public static final String COL_TARGET_ID = "target_id";

    public static final String CACHE_NAME_COMMENT="commentCache";
    public static final String CACHE_NAME_COMMENT_PAGE="commentPageCache";
}
