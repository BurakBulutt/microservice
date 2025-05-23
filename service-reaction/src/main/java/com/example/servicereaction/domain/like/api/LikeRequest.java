package com.example.servicereaction.domain.like.api;

import com.example.servicereaction.domain.like.enums.LikeTarget;
import com.example.servicereaction.domain.like.enums.LikeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LikeRequest(
        @NotNull(message = "validation.like.userId.notNull")
        @NotBlank(message = "validation.like.userId.notBlank")
        String userId,
        @NotNull(message = "validation.like.targetId.notNull")
        @NotBlank(message = "validation.like.targetId.notBlank")
        String targetId,
        @NotNull(message = "validation.like.likeType.notNull")
        LikeType likeType,
        @NotNull(message = "validation.like.likeTarget.notNull")
        LikeTarget likeTarget
) {
}
