package com.example.servicemedia.media.api;

import com.example.servicemedia.media.dto.MediaSourceDto;

import java.util.List;

public record MediaSourceRequest(
        List<MediaSourceDto> mediaSources
) {
}
