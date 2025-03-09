package com.example.servicereaction.comment.service;

import com.example.servicereaction.comment.feign.UserResponse;
import com.example.servicereaction.comment.dto.CommentDto;
import com.example.servicereaction.comment.enums.CommentType;
import com.example.servicereaction.comment.feign.UserFeignClient;
import com.example.servicereaction.comment.mapper.CommentServiceMapper;
import com.example.servicereaction.comment.model.Comment;
import com.example.servicereaction.comment.repo.CommentRepository;
import com.example.servicereaction.like.service.LikeService;
import com.example.servicereaction.util.rest.BaseException;
import com.example.servicereaction.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserFeignClient userFeignClient;
    private final LikeService likeService;

    @Override
    public Page<CommentDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toCommentDto);
    }

    @Override
    public CommentDto getById(String id) {
        return repository.findById(id).map(this::toCommentDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
    }

    @Override
    public Page<CommentDto> getByTargetId(String targetId, String ownerId, Pageable pageable) {
        return repository.findAllByTargetIdAndParentNull(targetId, pageable).map(comment -> {
            CommentDto commentDto = CommentServiceMapper.toDto(comment,getUser(comment.getUserId()));
            commentDto.setLikeCount(likeService.findLikeCount(comment.getId(), ownerId));
            if (comment.getCommentList() != null && !comment.getCommentList().isEmpty()) {
                commentDto.setCommentList(comment.getCommentList().stream().map(comment1 -> {
                    CommentDto dto = CommentServiceMapper.toDto(comment1,getUser(comment1.getUserId()));
                    dto.setLikeCount(likeService.findLikeCount(comment1.getId(), ownerId));
                    return dto;
                }).toList());
            }
            return commentDto;
        });
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
        return toCommentDto(repository.save(CommentServiceMapper.toEntity(new Comment(), commentDto, parent)));
    }

    @Override
    @Transactional
    public CommentDto update(String id, CommentDto commentDto) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
        comment.setContent(commentDto.getContent());
        return toCommentDto(repository.save(comment));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Comment comment = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Comment.class.getSimpleName(), id));
        repository.delete(comment);
        //TODO id --> targetId likeleri sildirecek event.
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = CommentServiceMapper.toDto(comment, getUser(comment.getUserId()));
        if (comment.getParent() != null) {
            CommentDto parent = CommentServiceMapper.toDto(comment.getParent(), getUser(comment.getParent().getUserId()));
            parent.setLikeCount(likeService.findLikeCount(comment.getParent().getId(),null));
            dto.setParent(parent);
        }
        dto.setLikeCount(likeService.findLikeCount(comment.getId(),null));
        if (comment.getCommentList() != null && !comment.getCommentList().isEmpty()) {
            dto.setCommentList(comment.getCommentList().stream()
                    .map(this::toCommentDto)
                    .toList());
        }
        return dto;
    }

    private UserResponse getUser(String userId) {
        ResponseEntity<UserResponse> userResponse = userFeignClient.getById(userId);
        return userResponse.getBody();
    }
}
