package com.example.servicemedia.domain.category.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.category.constants.CategoryConstants;
import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.category.repo.CategoryRepository;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.servicemedia.util.CreatorComponent.fullTextSearchQuery;
import static com.example.servicemedia.util.CreatorComponent.slugGenerator;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
@CacheConfig(cacheNames = CategoryConstants.CACHE_NAME_CATEGORY)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CacheManager cacheManager;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    @Cacheable(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE, key = "'category-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<CategoryDto> getAll(Pageable pageable) {
        log.info("Getting all categories");
        return repository.findAll(pageable).map(CategoryServiceMapper::toDto);
    }

    @Override
    @Cacheable(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE ,key = "'category-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()",condition = "#query == null")
    public Page<CategoryDto> filter(Pageable pageable,String query) {
        log.info("Getting filtered categories: [query: {}]",query);

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (query != null && query.length() >= 2) {
            queryBuilder.must(fullTextSearchQuery(query));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticCategory> search = elasticsearchOperations.search(nativeQuery, ElasticCategory.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return new PageImpl<>(repository.findAllByIdIn(ids,nativeQuery.getSort()),pageable,search.getTotalHits()).map(CategoryServiceMapper::toDto);
    }

    @Override
    public Long count() {
        log.info("Getting categories count");
        return repository.count();
    }

    @Override
    @Cacheable(key = "'category-id:' + #id")
    public CategoryDto getById(String id) {
        log.info("Getting category by id: {}", id);
        return repository.findById(id).map(CategoryServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    public Category findOrCreateByName(String name) {
        Optional<Category> category = repository.findByNameContainsIgnoreCase(name).stream().findAny();
        return category.orElseGet(() -> repository.save(new Category(name, name, slugGenerator(name), Collections.emptyList())));
    }

    @Override
    public Set<Category> findAllByIds(Set<String> ids) {
        log.info("Getting categories by ids: {}", ids);
        return new HashSet<>(repository.findAllById(ids));
    }

    @Override
    @Cacheable(key = "'category-slug:' + #slug")
    public CategoryDto getBySlug(String slug) {
        log.info("Getting category by slug: {}", slug);
        return repository.findBySlug(slug).map(category -> {
            CategoryDto dto = CategoryServiceMapper.toDto(category);
            dto.setContents(category.getContentList().stream().map(ContentServiceMapper::toDto).toList());
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'category-id:' + #result.id"),
                    @CachePut(key = "'category-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE, allEntries = true)
    )
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("Saving category: {}", categoryDto);
        return CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(new Category(),categoryDto)));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'category-id:' + #id"),
                    @CachePut(key = "'category-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE, allEntries = true)
    )
    public CategoryDto update(String id, CategoryDto categoryDto) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));

        log.info("Updating category: {}, updated: {}",id,categoryDto);
        return CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(category,categoryDto)));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE, allEntries = true),
            @CacheEvict(key = "'category-id:' + #id")
    })
    public void delete(String id) {
        Category category = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Category.class.getSimpleName(), id));

        for (Content content : category.getContentList()) {
            content.getCategories().remove(category);
        }

        log.info("Deleting category: {}, updated: {}",id,category);
        repository.delete(category);

        Cache cache = cacheManager.getCache(CategoryConstants.CACHE_NAME_CATEGORY);

        if (cache != null) {
            cache.evict("category-slug:" + category.getSlug());
        }
    }
}
