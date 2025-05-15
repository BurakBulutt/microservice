package com.example.servicemedia.content.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentNameResponse {
    private String id;
    private String name;
}
