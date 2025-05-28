package com.example.servicemedia.domain.category.elasticsearch.event;

import com.example.servicemedia.domain.category.dto.CategoryDto;

public record CreateCategoryEvent(
        CategoryDto category
) {
}
