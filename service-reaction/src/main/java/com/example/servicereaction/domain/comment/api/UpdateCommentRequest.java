package com.example.servicereaction.domain.comment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotNull(message = "validation.comment.content.notNull")
        @NotBlank(message = "validation.comment.content.notBlank")
        @Size(min = 5, max = 500, message = "validation.comment.content.size")
        String content
) {
}
