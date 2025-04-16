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
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final CategoryService categoryService;
    private final LikeFeignClient likeFeignClient;
    private final StreamBridge streamBridge;


    @Override
    public Page<ContentDto> getAll(Pageable pageable,String name) {
        log.info("Getting all contents");
        if (StringUtils.hasLength(name)){
            return repository.findAllByNameContainsIgnoreCase(name,pageable).map(this::toContentDto);
        }
        return repository.findAll(pageable).map(this::toContentDto);
    }

    @Override
    public Page<ContentDto> filter(String categoryId, String sortBy, Pageable pageable) {
        Page<Content> contents;
        Sort sort;

        switch (sortBy) {
            case "name" -> sort = Sort.by(Sort.Direction.ASC, "name");
            case "newest" -> sort = Sort.by(Sort.Direction.DESC, "startDate");
            default -> sort = Sort.unsorted();
        }

        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),sort);

        if (categoryId != null) {
            contents = repository.findAllByCategoryId(categoryId, pageRequest);
        } else {
            contents = repository.findAll(pageRequest);
        }

        log.info("Getting filtered contents: [category: {}, sort: {}]",categoryId,sortBy);
        return contents.map(this::toContentDto);
    }

    @Override
    public List<ContentDto> searchFilter(String query) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageRequest = PageRequest.of(0, 4, sort);

        log.info("Getting searched contents: {}",query);
        return repository.findAllByNameContainsIgnoreCase(query, pageRequest).map(this::toContentDto).getContent();
    }

    @Override
    public Page<ContentDto> getNewContents() {
        Pageable pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "created"));
        log.info("Getting new contents");
        return repository.findNewContents(pageRequest).map(this::toContentDto);
    }

    @Override
    public ContentDto getById(String id) {
        return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    public Content findById(String id) {
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
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
    public void update(String id, ContentDto contentDto) {
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
        log.warn("Update content: {}, updated: {}",id,contentDto);
        repository.save(ContentServiceMapper.toEntity(content, contentDto));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        Set<String> mediaIds = content.getMedias().stream().map(Media::getId).collect(Collectors.toSet());
        repository.delete(content);
        log.warn("Content is deleted: {}", id);

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
