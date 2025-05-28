package com.example.servicemedia.domain.content.elasticsearch.event;

import com.example.servicemedia.domain.content.dto.ContentDto;

public record CreateContentEvent(
        ContentDto content
) {
}
