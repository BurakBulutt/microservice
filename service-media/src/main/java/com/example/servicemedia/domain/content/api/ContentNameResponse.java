package com.example.servicemedia.domain.content.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentNameResponse {
    private String id;
    private String name;
}
