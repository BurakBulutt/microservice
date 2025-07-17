package com.example.servicemedia.domain.content.api;

import com.example.servicemedia.domain.content.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Set;

public record ContentRequest(
        @NotNull(message = "validation.content.name.notNull")
        @NotBlank(message = "validation.content.name.notBlank")
        String name,
        String description,
        String photoUrl,
        @NotNull(message = "validation.content.type.notNull")
        ContentType type,
        String subject,
        LocalDate startDate,
        @NotNull(message = "validation.content.slug.notNull")
        @NotBlank(message = "validation.content.slug.notBlank")
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",message = "validation.content.slug.invalidPattern")
        String slug,
        Set<String> categoryIds,
        @Positive(message = "validation.content.episodeTime.positive")
        Integer episodeTime
) {
}
