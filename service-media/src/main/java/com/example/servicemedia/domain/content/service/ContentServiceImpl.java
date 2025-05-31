package com.example.servicemedia.domain.content.service;


import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.category.service.CategoryService;
import com.example.servicemedia.domain.content.constants.ContentConstants;
import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.elasticsearch.event.BulkContentCreateEvent;
import com.example.servicemedia.domain.content.elasticsearch.event.CreateContentEvent;
import com.example.servicemedia.domain.content.elasticsearch.event.DeleteContentEvent;
import com.example.servicemedia.domain.content.elasticsearch.event.UpdateContentEvent;
import com.example.servicemedia.domain.content.elasticsearch.model.ElasticContent;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.content.repo.ContentRepository;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.service.FansubService;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.elasticsearch.event.BulkMediaCreateEvent;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.feign.like.LikeType;
import com.example.servicemedia.domain.media.mapper.MediaServiceMapper;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.domain.media.model.MediaSource;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.servicemedia.util.CreatorComponent.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@CacheConfig(cacheNames = ContentConstants.CACHE_NAME_CONTENT)
public class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final CategoryService categoryService;
    private final LikeFeignClient likeFeignClient;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;
    private final FansubService fansubService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ApplicationEventPublisher publisher;

    @Override
    @Cacheable(cacheNames = ContentConstants.CACHE_NAME_CONTENT_PAGE, key = "'content-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<ContentDto> getAll(Pageable pageable) {
        log.info("Getting all contents");
        return repository.findAll(pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(cacheNames = ContentConstants.CACHE_NAME_CONTENT_PAGE, key = "'content-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()",
            condition = "#category == null and #query == null and #firstDate == null and #lastDate == null and #type == null")
    public Page<ContentDto> filter(Pageable pageable, String category, String query, LocalDate firstDate, LocalDate lastDate, ContentType type) {
        log.info("Getting filtered contents: [category: {}, firstDate: {}, lastDate: {}, query: {}]", category, firstDate, lastDate, query);

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (query != null && query.length() >= 2) {
            queryBuilder.must(fullTextSearchQuery(query));
        }

        if (category != null) {
            queryBuilder.filter(QueryBuilders.term()
                    .field("categories")
                    .value(category)
                    .build()
                    ._toQuery());
        }

        if (firstDate != null && lastDate != null) {
            queryBuilder.filter(QueryBuilders.range()
                    .date((builder -> builder
                            .field("startDate")
                            .gte(firstDate.toString())
                            .lte(lastDate.toString())
                            .format("yyyy-MM-dd")))
                    .build()._toQuery());
        }

        if (type != null) {
            queryBuilder.filter(QueryBuilders.term()
                    .field("type")
                    .value(type.name())
                    .build()
                    ._toQuery());
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticContent> search = elasticsearchOperations.search(nativeQuery, ElasticContent.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return new PageImpl<>(repository.findAllByIdIn(ids,nativeQuery.getSort()),pageable,search.getTotalHits()).map(this::toContentDto);
    }

    @Override
    public List<ContentDto> search(String query) {
        List<HighlightField> fields = List.of(new HighlightField(ContentConstants.SEARCH_FIELD_NAME));
        HighlightParameters parameters = HighlightParameters.builder()
                .withPreTags(ContentConstants.HIGHLIGHT_PRE_TAG)
                .withPostTags(ContentConstants.HIGHLIGHT_POST_TAG)
                .withType("fvh")
                .build();
        Highlight highlight = new Highlight(parameters, fields);
        NativeQuery nativeQuery = NativeQuery.builder().withQuery(fullTextSearchQuery(query)).withHighlightQuery(new HighlightQuery(highlight, ElasticContent.class)).withMaxResults(4).build();
        SearchHits<ElasticContent> search = elasticsearchOperations.search(nativeQuery, ElasticContent.class);

        List<ContentDto> contentDtoList = new ArrayList<>();

        search.getSearchHits().forEach(hit -> createContentDtoFromHit(contentDtoList, hit));
        return contentDtoList;
    }

    @Override
    @Cacheable(key = "'content-id:' + #id")
    public ContentDto getById(String id) {
        return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    @Cacheable(key = "'content-slug:' + #slug")
    public ContentDto getBySlug(String slug) {
        log.info("Getting content by slug: {}", slug);
        return repository.findBySlug(slug).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), slug));
    }

    @Override
    public ContentDto getByTop(LikeType likeType) {
        ResponseEntity<String> getByTopTarget = likeFeignClient.getTopTarget(likeType);
        if (getByTopTarget.hasBody()) {
            String id = getByTopTarget.getBody();
            assert id != null;
            return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        }
        throw new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName());
    }

    @Override
    public Long getCount() {
        return repository.count();
    }

    @Override
    public Content findById(String id) {
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'content-id:' + #result.id"),
                    @CachePut(key = "'content-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = ContentConstants.CACHE_NAME_CONTENT_PAGE, allEntries = true)
    )
    public ContentDto save(ContentDto contentDto) {
        Content content = ContentServiceMapper.toEntity(new Content(), contentDto);
        content.setMedias(Collections.emptyList());
        content.setCategories(Collections.emptyList());
        if (!contentDto.getCategories().isEmpty()) {
            Set<String> requestCategoryIds = contentDto.getCategories().stream()
                    .map(CategoryDto::getId)
                    .collect(Collectors.toSet());
            List<Category> categories = categoryService.findAllByIds(requestCategoryIds).stream().toList();
            content.setCategories(categories);
        }
        log.warn("Saving content: {}", contentDto);
        ContentDto dto = toContentDto(repository.save(content));
        publisher.publishEvent(new CreateContentEvent(dto));
        return dto;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = ContentConstants.CACHE_NAME_CONTENT_PAGE, allEntries = true)
    public void saveContentsBulk(List<ContentDto> contentDtoList) {
        log.info("Contents are Saving: {}", contentDtoList.toString());
        List<Content> contentList = new ArrayList<>();

        contentDtoList.forEach(contentDto -> {
            contentDto.setSlug(slugGenerator(contentDto.getName()));
            Content content = ContentServiceMapper.toEntity(new Content(), contentDto);

            List<Media> medias = new ArrayList<>();
            content.setMedias(medias);

            contentDto.getMedias().forEach(mediaDto -> {
                Media media = MediaServiceMapper.toEntity(new Media(), content, mediaDto);

                List<MediaSource> mediaSources = new ArrayList<>();
                media.setMediaSources(mediaSources);

                mediaDto.getMediaSourceList().forEach(mediaSourceDto -> {
                    MediaSource mediaSource = new MediaSource();
                    Fansub fansub = fansubService.findOrCreateByName(mediaSourceDto.getFansub().getName());

                    mediaSource.setMedia(media);
                    mediaSource.setFansub(fansub);
                    mediaSource.setUrl(mediaSourceDto.getUrl());
                    mediaSource.setType(mediaSourceDto.getType());

                    mediaSources.add(mediaSource);
                });
                medias.add(media);
            });

            if (contentDto.getCategories() != null && !contentDto.getCategories().isEmpty()) {
                Set<String> categoryIds = getCategoryIds(new HashSet<>(), contentDto);

                List<Category> categories = categoryService.findAllByIds(categoryIds).stream().toList();
                content.setCategories(categories);
            }
            contentList.add(content);
        });

        List<Content> savedList = repository.saveAllAndFlush(contentList);
        List<ContentDto> savedDtoList = savedList.stream()
                .map(c -> {
                    ContentDto dto = ContentServiceMapper.toDto(c);
                    dto.setCategories(c.getCategories().stream().map(CategoryServiceMapper::toDto).toList());
                    return dto;
                }).toList();
        List<MediaDto> savedMediaDtoList = savedList.stream()
                .flatMap(c -> c.getMedias().stream().map(media -> {
                    MediaDto dto = MediaServiceMapper.toDto(media);
                    dto.setContent(ContentServiceMapper.toDto(c));
                    return dto;
                }))
                .toList();
        publisher.publishEvent(new BulkContentCreateEvent(savedDtoList));
        publisher.publishEvent(new BulkMediaCreateEvent(savedMediaDtoList));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'content-id:' + #id"),
                    @CachePut(key = "'content-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = ContentConstants.CACHE_NAME_CONTENT_PAGE, allEntries = true)
    )
    public ContentDto update(String id, ContentDto contentDto) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        content.setName(contentDto.getName());
        Set<String> requestCategoryIds = contentDto.getCategories().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toSet());
        List<Category> categories = content.getCategories();
        categories.removeIf(category -> !requestCategoryIds.contains(category.getId()));
        Set<String> willSaveCategories = requestCategoryIds.stream()
                .filter(id1 -> categories.stream()
                        .noneMatch(category -> category.getId().equals(id1)))
                .collect(Collectors.toSet());
        if (!willSaveCategories.isEmpty()) {
            categories.addAll(categoryService.findAllByIds(willSaveCategories));
        }
        content.getMedias().forEach(media -> {
            media.setName(nameGenerator(media.getCount(), content));
            media.setSlug(slugGenerator(media.getName()));
        });
        log.warn("Updating content: {}", id);
        ContentDto dto = toContentDto(repository.save(ContentServiceMapper.toEntity(content, contentDto)));
        publisher.publishEvent(new UpdateContentEvent(dto));
        return dto;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ContentConstants.CACHE_NAME_CONTENT_PAGE, allEntries = true),
            @CacheEvict(key = "'content-id:' + #id")
    })
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        Set<String> mediaIds = content.getMedias().stream().map(Media::getId).collect(Collectors.toSet());
        log.warn("Deleting content: {}", id);
        repository.delete(content);
        publisher.publishEvent(new DeleteContentEvent(content.getId()));

        Cache cache = cacheManager.getCache("contentCache");

        if (cache != null) {
            cache.evict("content-slug:" + content.getSlug());
        }

        Set<String> targetIds = new HashSet<>(mediaIds);
        targetIds.add(id);

        boolean deleteMediaComments = streamBridge.send("deleteComments-out-0", targetIds);
        log.info("Sending delete all content and media comments message: {}, status: {}", targetIds, deleteMediaComments);
    }

    private ContentDto toContentDto(Content content) {
        ContentDto dto = ContentServiceMapper.toDto(content);
        dto.setCategories(content.getCategories().stream().map(CategoryServiceMapper::toDto).toList());
        dto.setMedias(content.getMedias().stream().map(MediaServiceMapper::toDto).toList());
        dto.setLikeCount(getLikeCount(content.getId()));

        return dto;
    }

    private LikeCountResponse getLikeCount(String id) {
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(id);
        return response.getBody();
    }

    private void createContentDtoFromHit(List<ContentDto> contentDtoList, SearchHit<ElasticContent> hit) {
        List<String> highLightField = hit.getHighlightField(ContentConstants.SEARCH_FIELD_NAME);
        ElasticContent elasticContent = hit.getContent();
        ContentDto contentDto = new ContentDto();
        contentDto.setId(elasticContent.getId());
        contentDto.setName(highLightField.get(0) != null ? highLightField.get(0) : elasticContent.getName());
        contentDto.setSlug(elasticContent.getSlug());
        contentDto.setPhotoUrl(elasticContent.getPhotoUrl());
        contentDtoList.add(contentDto);
    }

    private Set<String> getCategoryIds(Set<String> categoryIds, ContentDto contentDto) {
        List<CategoryDto> categoryDtoList = contentDto.getCategories();
        categoryDtoList.forEach(categoryDto -> {
            Category c = categoryService.findOrCreateByName(categoryDto.getName());
            categoryIds.add(c.getId());
        });
        return categoryIds;
    }
}
