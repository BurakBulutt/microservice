package com.example.servicemedia.domain.content.elasticsearch.event;

import com.example.servicemedia.domain.content.model.Content;

public record SaveContentEvent(
        Content content
) {
}
