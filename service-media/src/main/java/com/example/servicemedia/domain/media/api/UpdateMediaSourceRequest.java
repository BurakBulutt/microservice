package com.example.servicemedia.domain.media.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateMediaSourceRequest(
        @NotNull(message = "validation.mediaSourceList.notNull")
        @Valid
        List<MediaSourceRequest> mediaSourceRequestList
) {
}
