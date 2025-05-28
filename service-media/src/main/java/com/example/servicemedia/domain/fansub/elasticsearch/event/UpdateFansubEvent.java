package com.example.servicemedia.domain.fansub.elasticsearch.event;

import com.example.servicemedia.domain.fansub.dto.FansubDto;

public record UpdateFansubEvent(
        FansubDto fansub
) {
}
