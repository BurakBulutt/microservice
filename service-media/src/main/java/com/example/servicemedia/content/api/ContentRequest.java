package com.example.servicemedia.content.api;

import com.example.servicemedia.content.enums.ContentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record ContentRequest(
    String name,
    String description,
    String photoUrl,
    ContentType type,
    String subject,
    LocalDate startDate,
    String slug,
    @NotEmpty(message = "validation.content.category.empty")
    @NotNull(message = "validation.content.category.null")
    Set<String> categoryIds,
    Integer episodeTime
) {
}
