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
import com.example.servicemedia.media.service.MediaService;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final MediaService mediaService;
    private final CategoryService categoryService;
    private final LikeFeignClient likeFeignClient;

    @Override
    @Transactional
    public Page<ContentDto> getAll(Pageable pageable) {
        log.info("Getting all contents...");
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

        return contents.map(this::toContentDto);
    }

    @Override
    public List<ContentDto> searchFilter(String query) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageRequest = PageRequest.of(0, 4, sort);

        return repository.findAllByNameContainsIgnoreCase(query, pageRequest).map(this::toContentDto).getContent();
    }

    @Override
    public Page<ContentDto> getNewContents() {
        Pageable pageRequest = PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "created"));
        return repository.findNewContents(pageRequest).map(this::toContentDto);
    }

    @Override
    public ContentDto getById(String id) {
        return repository.findById(id).map(this::toContentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    public ContentDto getBySlug(String slug) {
        return repository.findBySlug(slug).map(content -> {
            ContentDto dto = toContentDto(content);
            dto.setMediaList(mediaService.getByContentId(content.getId()));
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public void save(ContentDto contentDto) {
        Set<String> requestCategoryIds = contentDto.getCategories().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toSet());
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
        repository.save(ContentServiceMapper.toEntity(content, contentDto));
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        mediaService.deleteAllByContentId(content.getId());
        repository.delete(content); //TODO RABBIT ILE SAGA AKISI KURULMALIDIR
    }

    private ContentDto toContentDto(Content content) {
        ContentDto dto = ContentServiceMapper.toDto(content);
        dto.setCategories(content.getCategories().stream().map(CategoryServiceMapper::toDto).toList());
        String correlationId = MDC.get("correlationId");
        String userId = MDC.get("userId");
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(dto.getId(),correlationId,userId);
        if (response.getBody() != null) {
            dto.setLikeCount(response.getBody());
        }
        return dto;
    }
}
