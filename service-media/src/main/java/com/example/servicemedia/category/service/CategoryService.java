package com.example.servicemedia.category.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    Page<CategoryDto> getAll(Pageable pageable);
    Page<CategoryDto> filter(Pageable pageable,String name);

    Long count();

    CategoryDto getById(String id);

    List<Category> getByName(String name);

    CategoryDto getBySlug(String slug);

    Set<Category> getAllByIds(Set<String> ids);

    CategoryDto save(CategoryDto categoryDto);
    CategoryDto update(String id, CategoryDto categoryDto);
    void delete(String id);
}
