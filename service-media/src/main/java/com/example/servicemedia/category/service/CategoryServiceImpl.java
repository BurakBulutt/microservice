package com.example.servicemedia.category.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.category.model.Category;
import com.example.servicemedia.category.repo.CategoryRepository;
import com.example.servicemedia.category.repo.CategorySpec;
import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
@CacheConfig(cacheNames = "categoryCache")
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "categoryPageCache" ,key = "'category-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<CategoryDto> getAll(Pageable pageable) {
        log.info("Getting all categories");
        return repository.findAll(pageable).map(CategoryServiceMapper::toDto);
    }

    @Override
    @Cacheable(value = "categoryPageCache" ,key = "'category-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#name == null")
    public Page<CategoryDto> filter(Pageable pageable, String name) {
        log.info("Getting filtered categories");
        Specification<Category> specification = Specification.where(CategorySpec.nameContainsIgnoreCase(name));
        return repository.findAll(specification,pageable).map(CategoryServiceMapper::toDto);
    }

    @Override
    @Cacheable(key = "'category-id:' + #id")
    public CategoryDto getById(String id) {
        return repository.findById(id).map(CategoryServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));
    }

    @Override
    public Set<Category> getAllByIds(Set<String> ids) {
        return new HashSet<>(repository.findAllById(ids));
    }

    @Override
    public List<Category> getByName(String name) {
        return repository.findByNameContainsIgnoreCase(name);
    }

    @Override
    @Cacheable(key = "'category-slug:' + #slug")
    public CategoryDto getBySlug(String slug) {
        return repository.findBySlug(slug).map(category -> {
            CategoryDto dto = CategoryServiceMapper.toDto(category);
            dto.setContents(category.getContentList().stream().map(ContentServiceMapper::toDto).toList());
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        return CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(new Category(),categoryDto)));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'category-id:' + #id"),
                    @CachePut(key = "'category-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = "categoryPageCache", allEntries = true)
    )
    public CategoryDto update(String id, CategoryDto categoryDto) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));
        return CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(category,categoryDto)));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categoryPageCache", allEntries = true),
            @CacheEvict(key = "'category-id:' + #id")
    })
    public void delete(String id) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));

        for (Content content : category.getContentList()) {
            content.getCategories().remove(category);
        }

        repository.delete(category);

        Cache cache = cacheManager.getCache("categoryCache");

        if (cache != null) {
            cache.evict("category-slug:" + category.getSlug());
        }
    }
}
