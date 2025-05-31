package com.example.servicemedia.domain.fansub.elasticsearch.event;

import com.example.servicemedia.domain.fansub.model.Fansub;

public record SaveFansubEvent(
        Fansub fansub
) {
}
