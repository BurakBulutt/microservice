package com.example.servicemedia.content.api;

import com.example.servicemedia.content.enums.ContentType;

import java.util.Date;

public record ContentRequest(
    String name,
    String description,
    String photoUrl,
    ContentType type,
    String subject,
    Date startDate,
    String slug
) {
}
