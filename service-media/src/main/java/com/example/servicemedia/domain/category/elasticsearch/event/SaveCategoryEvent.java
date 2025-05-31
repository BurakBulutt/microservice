package com.example.servicemedia.domain.category.elasticsearch.event;

import com.example.servicemedia.domain.category.model.Category;

public record SaveCategoryEvent(
        Category category
) {
}
