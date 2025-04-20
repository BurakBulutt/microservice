package com.example.servicemedia.media.dto;

import com.example.servicemedia.media.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MediaSourceDto {
    private String id;
    private String url;
    private SourceType type;
    private String mediaId;
    private String fanSub;
}
