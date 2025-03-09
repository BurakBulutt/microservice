package com.example.servicemedia.category.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);
    CategoryDto getById(String id);

    Set<Category> getAllByIds(Set<String> ids);

    CategoryDto getBySlug(String slug);
    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(String id,CategoryDto categoryDto);
    void delete(String id);
}
