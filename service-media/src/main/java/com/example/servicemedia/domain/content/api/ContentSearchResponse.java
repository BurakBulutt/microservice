package com.example.servicemedia.domain.content.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentSearchResponse {
    private String id;
    private String name;
    private String photoUrl;
    private String slug;
}
