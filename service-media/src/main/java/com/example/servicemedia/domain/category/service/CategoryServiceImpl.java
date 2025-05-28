package com.example.servicemedia.domain.category.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.category.constants.CategoryConstants;
import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.elasticsearch.event.CreateCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.event.DeleteCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.event.UpdateCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.category.repo.CategoryRepository;
import com.example.servicemedia.domain.content.constants.ContentConstants;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
@CacheConfig(cacheNames = CategoryConstants.CACHE_NAME_CATEGORY)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CacheManager cacheManager;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ApplicationEventPublisher publisher;

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
        return repository.findAllByIdIn(ids,pageable).map(CategoryServiceMapper::toDto);
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
    public Set<Category> getAllByIds(Set<String> ids) {
        log.info("Getting categories by ids: {}", ids);
        return new HashSet<>(repository.findAllById(ids));
    }

    @Override
    public List<Category> getByName(String name) {
        log.info("Getting categories by name: {}", name);
        return repository.findByNameContainsIgnoreCase(name);
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
    @CacheEvict(value = CategoryConstants.CACHE_NAME_CATEGORY_PAGE, allEntries = true)
    public CategoryDto save(CategoryDto categoryDto) {
        log.info("Saving category: {}", categoryDto);
        CategoryDto dto = CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(new Category(),categoryDto)));
        publisher.publishEvent(new CreateCategoryEvent(dto));
        return dto;
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
        CategoryDto dto = CategoryServiceMapper.toDto(repository.save(CategoryServiceMapper.toEntity(category,categoryDto)));
        publisher.publishEvent(new UpdateCategoryEvent(dto));
        return dto;
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
        publisher.publishEvent(new DeleteCategoryEvent(id));

        Cache cache = cacheManager.getCache(CategoryConstants.CACHE_NAME_CATEGORY);

        if (cache != null) {
            cache.evict("category-slug:" + category.getSlug());
        }
    }

    private Query fullTextSearchQuery(String query) {
        return QueryBuilders.match()
                .field(ContentConstants.SEARCH_FIELD_NAME)
                .query(query)
                .fuzziness(ContentConstants.SEARCH_FUZZINESS)
                .build()
                ._toQuery();
    }
}
