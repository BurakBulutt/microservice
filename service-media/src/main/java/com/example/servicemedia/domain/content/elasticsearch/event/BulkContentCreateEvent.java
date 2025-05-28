package com.example.servicemedia.domain.content.elasticsearch.event;

import com.example.servicemedia.domain.content.dto.ContentDto;

import java.util.List;

public record BulkContentCreateEvent(
        List<ContentDto> contents
) {
}
