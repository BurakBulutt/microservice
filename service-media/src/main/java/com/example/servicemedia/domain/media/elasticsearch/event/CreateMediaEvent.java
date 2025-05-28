package com.example.servicemedia.domain.media.elasticsearch.event;

import com.example.servicemedia.domain.media.dto.MediaDto;

public record CreateMediaEvent(
        MediaDto media
) {
}
