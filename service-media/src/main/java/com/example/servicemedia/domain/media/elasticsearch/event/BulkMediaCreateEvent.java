package com.example.servicemedia.domain.media.elasticsearch.event;

import com.example.servicemedia.domain.media.dto.MediaDto;

import java.util.List;

public record BulkMediaCreateEvent(
        List<MediaDto> medias
) {
}
