package com.example.servicemedia.domain.fansub.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FansubResponse {
    private String id;
    private String name;
    private String url;
}
