package com.example.servicemedia.domain.content.service;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.domain.category.model.Category;
import com.example.servicemedia.domain.category.service.CategoryService;
import com.example.servicemedia.domain.content.constants.ContentConstants;
import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.content.repo.ContentRepository;
import com.example.servicemedia.domain.content.repo.ContentSpec;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.service.FansubService;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.feign.like.LikeType;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


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


    @Override
    @Cacheable(cacheNames = ContentConstants.CACHE_NAME_CONTENT_PAGE, key = "'content-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<ContentDto> getAll(Pageable pageable) {
        log.info("Getting all contents");
        return repository.findAll(pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(cacheNames = ContentConstants.CACHE_NAME_CONTENT_PAGE, key = "'content-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()",condition = "#category == null")
    public Page<ContentDto> filter(Pageable pageable,String category) {
        Specification<Content> spec = Specification.where(ContentSpec.byCategory(category));
        log.info("Getting filtered contents: [category: {}]",category);
        return repository.findAll(spec,pageable).map(this::toContentDto);
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
    @CacheEvict(value = ContentConstants.CACHE_NAME_CONTENT_PAGE, allEntries = true)
    public ContentDto save(ContentDto contentDto) {
        Content content = ContentServiceMapper.toEntity(new Content(), contentDto);
        if (!contentDto.getCategories().isEmpty()) {
            Set<String> requestCategoryIds = contentDto.getCategories().stream()
                    .map(CategoryDto::getId)
                    .collect(Collectors.toSet());
            List<Category> categories = categoryService.getAllByIds(requestCategoryIds).stream().toList();
            content.setCategories(categories);
        }
        log.warn("Saving content: {}", contentDto);
        return ContentServiceMapper.toDto(repository.save(content));
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

            List<MediaDto> mediaDtoList = contentDto.getMedias();
            mediaDtoList.forEach(mediaDto -> {
                Media media = new Media();

                media.setDescription(mediaDto.getDescription());
                media.setCount(mediaDto.getCount());
                media.setPublishDate(mediaDto.getPublishDate());
                media.setContent(content);
                media.setName(nameGenerator(content.getName(),media.getCount(),content.getType()));
                media.setSlug(slugGenerator(media.getName()));

                List<MediaSource> mediaSources = new ArrayList<>();
                media.setMediaSources(mediaSources);

                List<MediaSourceDto> mediaSourceDtoList = mediaDto.getMediaSourceList();
                mediaSourceDtoList.forEach(mediaSourceDto -> {
                    MediaSource mediaSource = new MediaSource();
                    Fansub fansub = fansubService.findOrCreateByName(mediaSourceDto.getFansub().getName());

                    mediaSource.setMedia(media);
                    mediaSource.setUrl(mediaSourceDto.getUrl());
                    mediaSource.setFansub(fansub);
                    mediaSource.setType(mediaSourceDto.getType());

                    mediaSources.add(mediaSource);
                });
                medias.add(media);
            });

            if (contentDto.getCategories() != null && !contentDto.getCategories().isEmpty()) {
                Set<String> categoryIds = new HashSet<>();

                List<CategoryDto> categoryDtoList = contentDto.getCategories();
                categoryDtoList.forEach(categoryDto -> {
                    Optional<Category> category = categoryService.getByName(categoryDto.getName()).stream().findAny();
                    category.ifPresentOrElse(category1 -> categoryIds.add(category1.getId()), () -> {
                        categoryDto.setSlug(slugGenerator(categoryDto.getName()));
                        CategoryDto dto1 = categoryService.save(categoryDto);
                        categoryIds.add(dto1.getId());
                    });
                });

                List<Category> categories = categoryService.getAllByIds(categoryIds).stream().toList();
                content.setCategories(categories);
            }

            contentList.add(content);
        });
        repository.saveAllAndFlush(contentList);
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
            categories.addAll(categoryService.getAllByIds(willSaveCategories));
        }
        log.warn("Updating content: {}", id);

        return toContentDto(repository.save(ContentServiceMapper.toEntity(content, contentDto)));
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
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(dto.getId());
        if (response.hasBody()) {
            dto.setLikeCount(response.getBody());
        }
        return dto;
    }

    private String slugGenerator(String name) {
        return name
                .toLowerCase()
                .trim()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("[\\s_-]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private String nameGenerator(String contentName, int count, ContentType type) {
        final String name;
        switch (type) {
            case MOVIE -> name = contentName;
            case SERIES -> name = contentName + " " + count + ". Bölüm";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return name;
    }
}
