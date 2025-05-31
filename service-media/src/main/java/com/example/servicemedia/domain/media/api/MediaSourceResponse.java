package com.example.servicemedia.domain.media.api;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.enums.SourceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaSourceResponse {
    private String id;
    private String url;
    private SourceType type;
    private MediaDto media;
    private FansubDto fansub;
}
