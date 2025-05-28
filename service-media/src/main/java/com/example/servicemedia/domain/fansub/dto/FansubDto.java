package com.example.servicemedia.domain.fansub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FansubDto {
    private String id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String name;
    private String url;
}
