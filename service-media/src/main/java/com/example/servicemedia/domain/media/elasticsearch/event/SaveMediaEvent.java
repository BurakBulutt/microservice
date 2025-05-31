package com.example.servicemedia.domain.media.elasticsearch.event;

import com.example.servicemedia.domain.media.model.Media;

public record SaveMediaEvent(
        Media media
) {
}
