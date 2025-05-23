package com.example.servicemedia.domain.media.api;

import com.example.servicemedia.domain.fansub.api.FansubRequest;
import com.example.servicemedia.domain.media.enums.SourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaSourceRequest(
        @NotNull(message = "validation.mediaSource.url.notNull")
        @NotBlank(message = "validation.mediaSource.url.notBlank")
        String url,
        @NotNull(message = "validation.mediaSource.type.notNull")
        SourceType type,
        @NotNull(message = "validation.mediaSource.mediaId.notNull")
        @NotBlank(message = "validation.mediaSource.mediaId.notBlank")
        String mediaId,
        @NotNull(message = "validation.mediaSource.fansub.notNull")
        @Valid
        FansubRequest fansub
) {
}
