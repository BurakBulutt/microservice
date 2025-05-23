package com.example.servicemedia.domain.media.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MediaRequest(
        String description,
        @NotNull(message = "validation.media.contentId.notNull")
        @NotBlank(message = "validation.media.contentId.notBlank")
        String contentId,
        @NotNull(message = "validation.media.count.notNull")
        @Min(value = 0,message = "validation.media.count.min")
        Integer count,
        LocalDate publishDate
) {
}
