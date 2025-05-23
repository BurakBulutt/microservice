package com.example.servicemedia.domain.category.service;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);
    Page<CategoryDto> filter(Pageable pageable);

    Long count();

    CategoryDto getById(String id);
    CategoryDto getBySlug(String slug);

    List<Category> getByName(String name);

    Set<Category> getAllByIds(Set<String> ids);

    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(String id, CategoryDto categoryDto);
    void delete(String id);
}
