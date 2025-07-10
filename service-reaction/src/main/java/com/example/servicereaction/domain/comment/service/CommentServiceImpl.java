package com.example.servicereaction.domain.comment.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicereaction.domain.comment.constants.CommentConstants;
import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import com.example.servicereaction.feign.TargetResponse;
import com.example.servicereaction.domain.comment.enums.CommentTargetType;
import com.example.servicereaction.feign.content.ContentFeignClient;
import com.example.servicereaction.feign.media.MediaFeignClient;
import com.example.servicereaction.feign.user.UserResponse;
import com.example.servicereaction.domain.comment.dto.CommentDto;
import com.example.servicereaction.feign.user.UserFeignClient;
import com.example.servicereaction.domain.comment.mapper.CommentServiceMapper;
import com.example.servicereaction.domain.comment.model.Comment;
import com.example.servicereaction.domain.comment.repo.CommentRepository;
import com.example.servicereaction.domain.like.service.LikeService;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.servicereaction.domain.comment.enums.CommentType.COMMENT;
import static com.example.servicereaction.domain.comment.enums.CommentType.REPLY;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@CacheConfig(cacheNames = CommentConstants.CACHE_NAME_COMMENT)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserFeignClient userFeignClient;
    private final ContentFeignClient contentFeignClient;
    private final MediaFeignClient mediaFeignClient;
    private final LikeService likeService;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    @Cacheable(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, key = "'comment-all:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<CommentDto> getAll(Pageable pageable) {
        log.info("Getting all comments");
        return repository.findAll(pageable).map(this::toCommentDto);
    }

    @Override
    @Cacheable(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, key = "'comment-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()", condition = "#query == null")
    public Page<CommentDto> filter(Pageable pageable, String query) {
        log.info("Getting all filtered comments");

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (StringUtils.hasLength(query)) {
            List<Query> queries = Stream.of("userId", "targetId", "parentId")
                    .map(field -> QueryBuilders.term(builder -> builder.field(field).value(query)))
                    .toList();

            queryBuilder.should(queries).minimumShouldMatch("1");
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticComment> search = elasticsearchOperations.search(nativeQuery, ElasticComment.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return new PageImpl<>(repository.findAllByIdIn(ids, nativeQuery.getSort()), pageable, search.getTotalHits()).map(this::toCommentDto);
    }

    @Override
    @Cacheable(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, key = "'comment-main:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<CommentDto> getAllByParentIsNull(Pageable pageable) {
        log.info("Getting all comments by parent is null");
        return repository.findAllByParentIsNull(pageable).map(this::toCommentDto);
    }

    @Override
    @Cacheable(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, key = "'comment-target:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString() + '_' + #target")
    public Page<CommentDto> getByTarget(Pageable pageable, String target) {
        log.info("Getting all comments for target: {}", target);
        return repository.findAllByTargetIdAndParentIsNull(pageable, target).map(this::toCommentDto);
    }

    @Override
    @Cacheable(key = "'comment-id:' + #id")
    public CommentDto getById(String id) {
        log.info("Getting comment: {}", id);
        return repository.findById(id).map(this::toCommentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
    }

    @Override
    @Transactional
    @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true)
    @Caching(
            put = @CachePut(key = "'comment-id:' + #result.id"),
            evict = @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true)
    )
    public CommentDto save(CommentDto commentDto) {
        Comment parent = null;

        if (commentDto.getCommentType() == REPLY) {
            if (commentDto.getParent().getId() == null || commentDto.getTargetId() != null) {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }

            parent = repository.findById(commentDto.getParent().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), commentDto.getParent().getId()));

            if (parent.getCommentType() != COMMENT) {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }

            commentDto.setTargetId(parent.getTargetId());
        } else {
            if (commentDto.getParent().getId() != null || commentDto.getTargetId() == null) {
                throw new BaseException(MessageResource.BAD_REQUEST);
            }
        }

        log.info("Saving comment: {}", commentDto);
        return toCommentDto(repository.save(CommentServiceMapper.toEntity(new Comment(), commentDto, parent)));
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(key = "'comment-id:' + #id"),
            evict = @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true)
    )
    public CommentDto update(String id, CommentDto commentDto) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
        comment.setContent(commentDto.getContent());

        log.info("Updating comment: {}, updated: {}", id, commentDto);
        return toCommentDto(repository.save(comment));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true),
            @CacheEvict(key = "'comment-id:' + #id")
    })
    public void delete(String id) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));

        log.warn("Deleting comment: {}", id);
        repository.delete(comment);

        log.warn("Deleting comment likes: {}", id);
        likeService.deleteLikesByTargetId(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true)
    public void deleteAllByTargetIdIn(Set<String> targetIds) {
        List<Comment> commentList = repository.findAllByTargetIdIn(targetIds);
        deleteCommentAndLikes(commentList);
        deleteCommentCache(commentList);

        boolean deleteLikes = streamBridge.send("deleteLikes-out-0", targetIds);
        log.info("Send delete all target likes message: {}, status: {}", targetIds, deleteLikes);
    }

    @Override
    @Transactional
    @CacheEvict(value = CommentConstants.CACHE_NAME_COMMENT_PAGE, allEntries = true)
    public void deleteUserComments(String userId) {
        List<Comment> commentList = repository.findAllByUserId(userId);
        deleteCommentAndLikes(commentList);
        deleteCommentCache(commentList);

        boolean deleteLikes = streamBridge.send("deleteUserLikes-out-0", userId);
        log.info("Send delete user likes message: {}, status: {}", userId, deleteLikes);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteCommentAndLikes(List<Comment> commentList) {
        Set<String> commentIds = commentList.stream().map(Comment::getId).collect(Collectors.toSet());

        log.warn("Deleting comments: {}", commentIds);
        repository.deleteAllById(commentIds);

        log.warn("Deleting comments likes: {}", commentIds);
        likeService.deleteLikesByTargetIdIn(commentIds);
    }

    private void deleteCommentCache(List<Comment> commentList) {
        Cache commentCache = cacheManager.getCache(CommentConstants.CACHE_NAME_COMMENT);
        if (commentCache != null) {
            for (Comment comment : commentList) {
                commentCache.evict("comment-id:" + comment.getId());
            }
        }
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = CommentServiceMapper.toDto(comment);
        initDto(dto, comment);

        if (comment.getParent() != null) {
            CommentDto parentDto = CommentServiceMapper.toDto(comment.getParent());
            initDto(parentDto, comment.getParent());
            dto.setParent(parentDto);
        }
        if (comment.getCommentList() != null && !comment.getCommentList().isEmpty()) {
            dto.setCommentList(comment.getCommentList().stream()
                    .map(this::toCommentDto)
                    .toList());
        }

        return dto;
    }

    private void initDto(CommentDto dto, Comment comment) {
        dto.setLikeCount(likeService.findLikeCount(comment.getId()));
        dto.setUser(getUser(comment.getUserId()));
        dto.setTarget(getTarget(comment.getTargetId(), comment.getTargetType()));
    }

    private UserResponse getUser(String userId) {
        ResponseEntity<UserResponse> userResponse = userFeignClient.getById(userId);
        return userResponse.getBody();
    }

    private TargetResponse getTarget(String targetId, CommentTargetType targetType) {
        ResponseEntity<? extends TargetResponse> targetResponse;

        switch (targetType) {
            case CONTENT -> targetResponse = contentFeignClient.getById(targetId);
            case MEDIA -> targetResponse = mediaFeignClient.getById(targetId);
            default -> throw new IllegalArgumentException("Unsupported target type: " + targetType);
        }

        return targetResponse.getBody();
    }
}
