package com.example.servicemedia.domain.fansub.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FansubRequest(
        @NotNull(message = "validation.fansub.name.notNull")
        @NotBlank(message = "validation.fansub.name.notBlank")
        String name,
        String url
) {
}
