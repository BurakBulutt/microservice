package com.example.servicemedia.domain.category.service;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);
    Page<CategoryDto> filter(Pageable pageable,String query);

    Long count();

    CategoryDto getById(String id);
    CategoryDto getBySlug(String slug);

    Category findOrCreateByName(String name);
    Set<Category> findAllByIds(Set<String> ids);

    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(String id, CategoryDto categoryDto);
    void delete(String id);
}
