package com.example.servicemedia.content.service;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.content.repo.ContentRepository;
import com.example.servicemedia.media.service.MediaService;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentServiceImpl implements ContentService{
    private final ContentRepository repository;
    private final MediaService mediaService;

    @Override
    public Page<ContentDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(content -> {
            ContentDto dto = ContentServiceMapper.toDto(content);
            dto.setMediaList(mediaService.getByContentId(content.getId()));
            return dto;
        });
    }

    @Override
    public Page<ContentDto> getNewContents(Pageable pageable) {
        return repository.findNewContents(pageable).map(content -> {
            ContentDto dto = ContentServiceMapper.toDto(content);
            dto.setMediaList(mediaService.getByContentId(content.getId()));
            return dto;
        });
    }

    @Override
    public ContentDto getById(String id) {
        return repository.findById(id).map(ContentServiceMapper::toDto).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND, Content.class.getSimpleName(),id));
    }

    @Override
    public ContentDto getBySlug(String slug) {
        return repository.findBySlug(slug).map(content -> {
            ContentDto dto = ContentServiceMapper.toDto(content);
            dto.setMediaList(mediaService.getByContentId(content.getId()));
            return dto;
        }).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Content.class.getSimpleName(),slug));
    }

    @Override
    @Transactional
    public ContentDto save(ContentDto contentDto) {
        return ContentServiceMapper.toDto(repository.save(ContentServiceMapper.toEntity(new Content(),contentDto)));
    }

    @Override
    @Transactional
    public ContentDto update(String id, ContentDto contentDto) {
        Content content = repository.findById(id).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Content.class.getSimpleName(),id));
        return ContentServiceMapper.toDto(repository.save(ContentServiceMapper.toEntity(content,contentDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Content content = repository.findById(id).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Content.class.getSimpleName(),id));
        mediaService.deleteAllByContentId(content.getId());
        repository.delete(content);
    }
}
