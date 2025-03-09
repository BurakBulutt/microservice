package com.example.servicemedia.content.service;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.category.mapper.CategoryServiceMapper;
import com.example.servicemedia.category.model.Category;
import com.example.servicemedia.category.service.CategoryService;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.content.repo.ContentRepository;
import com.example.servicemedia.feign.LikeCountResponse;
import com.example.servicemedia.feign.LikeFeignClient;
import com.example.servicemedia.media.service.MediaService;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService {
    private final ContentRepository repository;
    private final MediaService mediaService;
    private final CategoryService categoryService;
    private final LikeFeignClient likeFeignClient;

    @Override
    public Page<ContentDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(content -> toContentDto(content, null));
    }

    @Override
    public Page<ContentDto> getNewContents(Pageable pageable) {
        return repository.findNewContents(pageable).map(content -> toContentDto(content, null));
    }

    @Override
    public ContentDto getById(String id) {
        return repository.findById(id).map(content -> toContentDto(content, null)).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
    }

    @Override
    public ContentDto getBySlug(String slug, String ownerId) {
        return repository.findBySlug(slug).map(content -> {
            ContentDto dto = toContentDto(content, ownerId);
            dto.setMediaList(mediaService.getByContentId(content.getId()));
            return dto;
        }).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public ContentDto save(ContentDto contentDto) {
        return ContentServiceMapper.toDto(repository.save(ContentServiceMapper.toEntity(new Content(), contentDto)));
    }

    @Override
    @Transactional
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
        return ContentServiceMapper.toDto(repository.save(ContentServiceMapper.toEntity(content, contentDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(), id));
        mediaService.deleteAllByContentId(content.getId());
        repository.delete(content); //TODO KAFKA ILE SAGA AKISI KURULMALIDIR
    }

    private ContentDto toContentDto(Content content, String ownerId) {
        ContentDto dto = ContentServiceMapper.toDto(content);
        ResponseEntity<LikeCountResponse> likeCountResponseResponseEntity = likeFeignClient.getLikeCount(content.getId(), ownerId);
        dto.setLikeCount(likeCountResponseResponseEntity.getBody());
        dto.setCategories(content.getCategories().stream().map(CategoryServiceMapper::toDto).toList());
        return dto;
    }
}
