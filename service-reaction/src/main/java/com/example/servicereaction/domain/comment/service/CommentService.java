package com.example.servicereaction.domain.comment.service;

import com.example.servicereaction.domain.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CommentService {
    Page<CommentDto> getAll(Pageable pageable);
    Page<CommentDto> filter(Pageable pageable,String query);

    CommentDto getById(String id);

    CommentDto save(CommentDto commentDto);
    CommentDto update(String id, CommentDto commentDto);

    void deleteAllByTargetIdIn(Set<String> targetIds);
    void delete(String id);
    void deleteUserComments(String userId);
}
