package com.example.servicereaction.comment.service;

import com.example.servicereaction.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CommentService {
    Page<CommentDto> getAll(Pageable pageable);
    Page<CommentDto> filter(Pageable pageable,String targetId);
    CommentDto getById(String id);

    void save(CommentDto commentDto);
    void update(String id, CommentDto commentDto);

    void deleteAllByTargetIdIn(Set<String> targetIds);
    void delete(String id);
    void deleteUserComments(String userId);
}
