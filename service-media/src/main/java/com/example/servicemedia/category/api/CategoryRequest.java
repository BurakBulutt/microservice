package com.example.servicemedia.category.api;

public record CategoryRequest(
        String name,
        String description,
        String slug
) {
}
