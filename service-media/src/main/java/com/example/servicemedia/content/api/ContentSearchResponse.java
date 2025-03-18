package com.example.servicemedia.content.api;

import com.example.servicemedia.content.enums.ContentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentSearchResponse {
    private final String name;
    private final String photoUrl;
    private final String slug;
    private final ContentType type;
}
