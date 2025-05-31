package com.example.servicemedia.domain.media.dto;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.media.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MediaSourceDto {
    private String id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String url;
    private SourceType type;
    private MediaDto media;
    private FansubDto fansub;
}
