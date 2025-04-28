package com.example.servicereaction.comment.service;

import com.example.servicereaction.comment.repo.CommentSpec;
import com.example.servicereaction.feign.UserResponse;
import com.example.servicereaction.comment.dto.CommentDto;
import com.example.servicereaction.comment.enums.CommentType;
import com.example.servicereaction.feign.UserFeignClient;
import com.example.servicereaction.comment.mapper.CommentServiceMapper;
import com.example.servicereaction.comment.model.Comment;
import com.example.servicereaction.comment.repo.CommentRepository;
import com.example.servicereaction.like.service.LikeService;
import com.example.servicereaction.util.rest.BaseException;
import com.example.servicereaction.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@CacheConfig(cacheNames = "commentCache")
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserFeignClient userFeignClient;
    private final LikeService likeService;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "commentPageCache" ,key = "'comment-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<CommentDto> getAll(Pageable pageable) {
        log.info("Getting all comments");
        return repository.findAll(pageable).map(this::toCommentDto);
    }

    @Override
    @Cacheable(value = "commentPageCache" ,key = "'comment-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#targetId == null")
    public Page<CommentDto> filter(Pageable pageable, String targetId) {
        Specification<Comment> specification = Specification.where(CommentSpec.byTargetId(targetId));
        log.info("Getting all filtered comments");
        return repository.findAll(specification,pageable).map(this::toCommentDto);
    }

    @Override
    @Cacheable(key = "'comment-id:' + #id")
    public CommentDto getById(String id) {
        log.info("Getting comment: {}", id);
        return repository.findById(id).map(this::toCommentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    public CommentDto save(CommentDto commentDto) {
        Comment parent = null;
        if (commentDto.getType() == CommentType.REPLY) {
            if (commentDto.getParent().getId() == null) {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }
            parent = repository.findById(commentDto.getParent().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), commentDto.getParent().getId()));
        } else {
            if (commentDto.getParent().getId() != null) {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }
        }
        log.warn("Saving comment: {}",commentDto);
        return CommentServiceMapper.toDto(repository.save(CommentServiceMapper.toEntity(new Comment(), commentDto, parent)), getUser(commentDto.getUser().getId()));
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(key = "'content-id:' + #id"),
            evict = @CacheEvict(value = "contentPageCache", allEntries = true)
    )
    public CommentDto update(String id, CommentDto commentDto) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
        comment.setContent(commentDto.getContent());
        log.warn("Updating comment: {}, updated: {}",id,commentDto);
        return toCommentDto(repository.save(comment));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "commentPageCache", allEntries = true),
            @CacheEvict(value = "commentCache", key = "'comment-id:' + #id")
    })
    public void delete(String id) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
        log.warn("Deleting comment: {}",id);
        repository.delete(comment);

        log.warn("Deleting comment likes: {}",id);
        likeService.deleteLikesByTargetId(comment.getTargetId());
    }

    @Override
    @Transactional
    @CacheEvict(value = "commentPageCache",allEntries = true)
    public void deleteAllByTargetIdIn(Set<String> targetIds) {
        List<Comment> commentList = repository.findAllByTargetIdIn(targetIds);
        deleteCommentAndLikes(commentList);

        deleteCommentCache(commentList);

        boolean deleteLikes = streamBridge.send("deleteLikes-out-0", targetIds);
        log.info("Sending delete all target likes message: {}, status: {}",targetIds,deleteLikes);
    }

    @Override
    @Transactional
    @CacheEvict(value = "commentPageCache",allEntries = true)
    public void deleteUserComments(String userId) {
        List<Comment> commentList = repository.findAllByUserId(userId);
        deleteCommentAndLikes(commentList);

        deleteCommentCache(commentList);

        boolean deleteLikes = streamBridge.send("deleteUserLikes-out-0", userId);
        log.info("Sending delete user likes message: {}, status: {}",userId,deleteLikes);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteCommentAndLikes(List<Comment> commentList) {
        Set<String> commentIds = commentList.stream().map(Comment::getId).collect(Collectors.toSet());
        repository.deleteAllById(commentIds);
        likeService.deleteLikesByTargetIdIn(commentIds);
        log.warn("Comments are deleted: {}",commentIds);
    }

    private void deleteCommentCache(List<Comment> commentList) {
        Cache commentCache = cacheManager.getCache("commentCache");
        if (commentCache != null) {
            for (Comment comment : commentList) {
                commentCache.evict("comment-id:" + comment.getId());
            }
        }
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = CommentServiceMapper.toDto(comment, getUser(comment.getUserId()));
        dto.setLikeCount(likeService.findLikeCount(comment.getId()));
        if (comment.getParent() != null && comment.getType() == CommentType.REPLY) {
            CommentDto parent = CommentServiceMapper.toDto(comment.getParent(), getUser(comment.getParent().getUserId()));
            parent.setLikeCount(likeService.findLikeCount(comment.getParent().getId()));
            dto.setParent(parent);
        }
        if (comment.getCommentList() != null && !comment.getCommentList().isEmpty()) {
            dto.setCommentList(comment.getCommentList().stream()
                    .map(this::toCommentDto)
                    .toList());
        }
        return dto;
    }

    private UserResponse getUser(String userId) {
        ResponseEntity<UserResponse> userResponse = userFeignClient.getById(userId);
        return userResponse.getBody() != null ? userResponse.getBody() : null;
    }
}
