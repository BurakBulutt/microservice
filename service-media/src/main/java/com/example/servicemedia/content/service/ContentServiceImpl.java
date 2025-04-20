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
import com.example.servicemedia.media.mapper.MediaServiceMapper;
import com.example.servicemedia.media.model.Media;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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


    @Override
    @Cacheable(key = "'content-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<ContentDto> getAll(Pageable pageable) {
        log.info("Getting all contents");
        return repository.findAll(pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(key = "'content-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#categoryId == null and #name == null")
    public Page<ContentDto> filter(Pageable pageable,String categoryId,String name) {
        log.info("Getting filtered contents: [category: {}, name: {}]",categoryId,name);
        return repository.filter(name,categoryId,pageable).map(this::toContentDto);
    }

    @Override
    @Cacheable(key = "'content-id:' + #id")
    public ContentDto getById(String id) {
        return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    public Content findById(String id) {
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    @Cacheable(key = "'content-slug:' + #slug")
    public ContentDto getBySlug(String slug) {
        log.info("Getting content by slug: {}",slug);
        return repository.findBySlug(slug).map(content -> {
            ContentDto dto = toContentDto(content);
            dto.setMedias(content.getMedias().stream().map(MediaServiceMapper::toDto).toList());
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public void save(ContentDto contentDto) {
        Set<String> requestCategoryIds = contentDto.getCategories().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toSet());
        log.warn("Saving content: {}",contentDto);
        Content content = repository.save(ContentServiceMapper.toEntity(new Content(), contentDto));
        content.setCategories(categoryService.getAllByIds(requestCategoryIds).stream().toList());
    }

    @Override
    @Transactional
    @CachePut(key = "'content-id:' + #id")
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
        log.warn("Updating content: {}",id);
        return ContentServiceMapper.toDto(repository.save(ContentServiceMapper.toEntity(content, contentDto)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "'content-id:' + #id")
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        Set<String> mediaIds = content.getMedias().stream().map(Media::getId).collect(Collectors.toSet());
        log.warn("Deleting content: {}", id);
        repository.delete(content);

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
        if (response.getBody() != null) {
            dto.setLikeCount(response.getBody());
        }
        return dto;
    }
}
