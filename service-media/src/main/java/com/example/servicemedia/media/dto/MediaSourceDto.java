package com.example.servicemedia.media.dto;

import com.example.servicemedia.media.enums.SourceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaSourceDto {
    @JsonIgnore
    private String id;
    private String url;
    private SourceType type;
    private String mediaId;
}
