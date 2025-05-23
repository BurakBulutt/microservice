package com.example.servicemedia.domain.fansub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FansubDto {
    private String id;
    private String name;
    private String url;
}
