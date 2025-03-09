package com.example.servicereaction.comment.service;

import com.example.servicereaction.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Page<CommentDto> getAll(Pageable pageable);
    CommentDto getById(String id);

    Page<CommentDto> getByTargetId(String targetId, String ownerId, Pageable pageable);

    CommentDto save(CommentDto commentDto);
    CommentDto update(String id,CommentDto commentDto);
    void delete(String id);
}
