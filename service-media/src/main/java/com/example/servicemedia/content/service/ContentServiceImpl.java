package com.example.servicemedia.content.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.category.model.Category;
import com.example.servicemedia.category.service.CategoryService;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.content.repo.ContentRepository;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.feign.like.LikeType;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import com.example.servicemedia.media.mapper.MediaServiceMapper;
import com.example.servicemedia.media.model.Media;
import com.example.servicemedia.media.model.MediaSource;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@CacheConfig(cacheNames = "contentCache")
public class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final CategoryService categoryService;
    private final LikeFeignClient likeFeignClient;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(cacheNames = "contentPageCache", key = "'content-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<ContentDto> getAll(Pageable pageable) {
        log.info("Getting all contents");
        return repository.findAll(pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(cacheNames = "contentPageCache", key = "'content-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()", condition = "#categoryId == null and #name == null")
    public Page<ContentDto> filter(Pageable pageable, String categoryId, String name) {
        log.info("Getting filtered contents: [category: {}, name: {}]", categoryId, name);
        return repository.filter(name, categoryId, pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(key = "'content-id:' + #id")
    public ContentDto getById(String id) {
        return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    public ContentDto getByTop(LikeType likeType) {
        ResponseEntity<String> getByTopTarget = likeFeignClient.getTopTarget(likeType);
        if (getByTopTarget.hasBody()) {
            String id = getByTopTarget.getBody();
            assert id != null;
            return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        }
        throw new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(),likeType);
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
    @Cacheable(key = "'content-slug:' + #slug")
    public ContentDto getBySlug(String slug) {
        log.info("Getting content by slug: {}", slug);
        return repository.findBySlug(slug).map(content -> {
            ContentDto dto = toContentDto(content);
            dto.setMedias(content.getMedias().stream().map(MediaServiceMapper::toDto).toList());
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public ContentDto save(ContentDto contentDto) {
        Set<String> requestCategoryIds = contentDto.getCategories().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toSet());
        log.warn("Saving content: {}", contentDto);
        List<Category> categories = categoryService.getAllByIds(requestCategoryIds).stream().toList();
        Content content = ContentServiceMapper.toEntity(new Content(), contentDto);
        content.setCategories(categories);
        return ContentServiceMapper.toDto(repository.save(content));
    }

    @Override
    @Transactional
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
                media.setSlug(slugGenerator(media.getName()));

                final String name;
                switch (content.getType()) {
                    case MOVIE -> name = content.getName();
                    case SERIES -> name = content.getName() + " " + media.getCount() + ". Bölüm";
                    default -> throw new IllegalStateException("Unexpected value: " + content.getType());
                }

                media.setName(name);

                List<MediaSource> mediaSources = new ArrayList<>();
                media.setMediaSources(mediaSources);

                List<MediaSourceDto> mediaSourceDtoList = mediaDto.getMediaSourceList();
                mediaSourceDtoList.forEach(mediaSourceDto -> {
                    MediaSource mediaSource = new MediaSource();
                    mediaSource.setMedia(media);
                    mediaSource.setUrl(mediaSourceDto.getUrl());
                    mediaSource.setFanSub(mediaSourceDto.getFanSub());
                    mediaSource.setType(mediaSourceDto.getType());

                    mediaSources.add(mediaSource);
                });
                medias.add(media);
            });

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

            contentList.add(content);
        });
        repository.saveAll(contentList);
        repository.flush();
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'content-id:' + #id"),
                    @CachePut(key = "'content-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = "contentPageCache", allEntries = true)
    )
    public ContentDto update(String id, ContentDto contentDto) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        Set<String> requestCategoryIds = contentDto.getCategories().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toSet());
        List<Category> categorySet = content.getCategories();
        categorySet.removeIf(category -> !requestCategoryIds.contains(category.getId()));
        Set<String> willSaveCategories = requestCategoryIds.stream()
                .filter(id1 -> categorySet.stream()
                        .noneMatch(category -> category.getId().equals(id1)))
                .collect(Collectors.toSet());
        if (!willSaveCategories.isEmpty()) {
            categorySet.addAll(categoryService.getAllByIds(willSaveCategories));
        }
        log.warn("Updating content: {}", id);

        return toContentDto(repository.save(ContentServiceMapper.toEntity(content, contentDto)));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "contentPageCache", allEntries = true),
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
}
