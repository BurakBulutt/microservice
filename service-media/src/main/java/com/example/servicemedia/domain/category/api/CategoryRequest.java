package com.example.servicemedia.domain.category.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CategoryRequest(
        @NotNull(message = "validation.category.name.notNull")
        @NotBlank(message = "validation.category.name.notBlank")
        String name,
        String description,
        @NotNull(message = "validation.category.slug.notNull")
        @NotBlank(message = "validation.category.slug.notBlank")
        @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",message = "validation.category.slug.invalidPattern")
        String slug
) {
}
