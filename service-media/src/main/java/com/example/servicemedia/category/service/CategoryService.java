package com.example.servicemedia.category.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);
    Page<CategoryDto> filter(Pageable pageable,String name);

    CategoryDto getById(String id);
    CategoryDto getBySlug(String slug);

    Set<Category> getAllByIds(Set<String> ids);

    void save(CategoryDto categoryDto);
    CategoryDto update(String id, CategoryDto categoryDto);
    void delete(String id);
}
