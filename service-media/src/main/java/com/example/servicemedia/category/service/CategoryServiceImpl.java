package com.example.servicemedia.category.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.category.model.Category;
import com.example.servicemedia.category.repo.CategoryRepository;
import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public Page<CategoryDto> getAll(Pageable pageable,String name) {
        log.info("Getting all categories");
        if (StringUtils.hasLength(name)){
            return repository.findAllByNameContainsIgnoreCase(name,pageable).map(CategoryServiceMapper::toDto);
        }
        return repository.findAll(pageable).map(CategoryServiceMapper::toDto);
    }

    @Override
    public CategoryDto getById(String id) {
        return repository.findById(id).map(CategoryServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));
    }

    @Override
    public Set<Category> getAllByIds(Set<String> ids) {
        return new HashSet<>(repository.findAllById(ids));
    }

    @Override
    public CategoryDto getBySlug(String slug) {
        return repository.findBySlug(slug).map(category -> {
            CategoryDto dto = CategoryServiceMapper.toDto(category);
            dto.setContents(category.getContentList().stream().map(ContentServiceMapper::toDto).toList());
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public void save(CategoryDto categoryDto) {
        repository.save(CategoryServiceMapper.toEntity(new Category(),categoryDto));
    }

    @Override
    @Transactional
    public void update(String id, CategoryDto categoryDto) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));
        repository.save(CategoryServiceMapper.toEntity(category,categoryDto));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));

        for (Content content : category.getContentList()) {
            content.getCategories().remove(category);
        }

        repository.delete(category);
    }
}
