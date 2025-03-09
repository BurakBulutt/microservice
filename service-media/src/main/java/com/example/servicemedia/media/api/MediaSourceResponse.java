package com.example.servicemedia.media.api;

import com.example.servicemedia.media.enums.SourceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaSourceResponse {
    private String id;
    private String url;
    private SourceType type;
    private String mediaId;
    private String fanSub;
}
