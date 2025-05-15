package com.example.servicemedia.media.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaNameResponse {
    private String id;
    private String name;
}
