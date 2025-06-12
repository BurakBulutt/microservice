package com.example.servicereaction.domain.comment.service;

import com.example.servicereaction.domain.comment.constants.CommentConstants;
import com.example.servicereaction.domain.comment.dto.CommentDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CommentService {
    Page<CommentDto> getAll(Pageable pageable);
    Page<CommentDto> filter(Pageable pageable,String query);

    @Cacheable(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, key = "'comment-target:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString() + '_' + #target")
    Page<CommentDto> getByTarget(Pageable pageable, String target);

    CommentDto getById(String id);

    CommentDto save(CommentDto commentDto);
    CommentDto update(String id, CommentDto commentDto);

    void deleteAllByTargetIdIn(Set<String> targetIds);
    void delete(String id);
    void deleteUserComments(String userId);
}
