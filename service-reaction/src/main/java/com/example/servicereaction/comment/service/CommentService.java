package com.example.servicereaction.comment.service;

import com.example.servicereaction.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface CommentService {
    Page<CommentDto> getAll(Pageable pageable);
    CommentDto getById(String id);
    Page<CommentDto> getByTargetId(String targetId, Pageable pageable);

    void save(CommentDto commentDto);
    void update(String id, CommentDto commentDto);

    void deleteAllByTargetId(String targetId);
    void deleteAllByTargetIdIn(Set<String> targetIds);
    void delete(String id);
    void deleteUserComments(String userId);
}
