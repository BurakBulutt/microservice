package com.example.servicereaction.domain.like.service;

import com.example.servicereaction.domain.like.constants.LikeConstants;
import com.example.servicereaction.domain.like.dto.LikeCountDto;
import com.example.servicereaction.domain.like.dto.LikeDto;
import com.example.servicereaction.domain.like.enums.LikeType;
import com.example.servicereaction.domain.like.mapper.LikeServiceMapper;
import com.example.servicereaction.domain.like.model.Like;
import com.example.servicereaction.domain.like.repo.LikeRepository;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
public class LikeServiceImpl implements LikeService {
    private final LikeRepository repository;

    @Override
    @Retry(name = "likeRetry")
    public LikeCountDto findLikeCount(String targetId) {
        Integer likeCount = repository.findTargetLikeCount(targetId,LikeType.LIKE);
        Integer dislikeCount = repository.findTargetLikeCount(targetId,LikeType.DISLIKE);
        Boolean isUserLiked = false;
        Boolean isUserDisliked = false;

        final String user = MDC.get("user");

        if (user != null && !user.equals(LikeConstants.ANONYMOUS)) {
            isUserLiked = repository.isUserLiked(targetId, user, LikeType.LIKE);
            isUserDisliked = repository.isUserLiked(targetId, user, LikeType.DISLIKE);
        }

        if (isUserLiked && isUserDisliked) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }

        log.info("Getting like count..., target: {}",targetId);

        return LikeCountDto.builder()
                .targetId(targetId)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .isUserLiked(isUserLiked)
                .isUserDisliked(isUserDisliked)
                .build();
    }

    @Override
    public String getTopContentLikeTarget(LikeType likeType) {
        return repository.findTopContentLikeTarget(likeType.name()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Like.class.getSimpleName()));
    }

    @Override
    @Transactional
    public void save(LikeDto likeDto) {
        Like like = repository.findByTargetIdAndUserIdAndLikeType(likeDto.getTargetId(), likeDto.getUserId(), likeDto.getLikeType()).orElse(null);
        if (like == null) {
            repository.save(LikeServiceMapper.toEntity(new Like(), likeDto));
            return;
        }
        repository.save(LikeServiceMapper.toEntity(like, likeDto));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Like like = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Like.class.getSimpleName(), id));
        repository.delete(like);
    }

    @Override
    @Transactional
    public void deleteLikesByTargetId(String targetId) {
        log.info("Deleting likes with targetId: {}",targetId);
        repository.deleteAllByTargetId(targetId);
    }

    @Override
    @Transactional
    public void deleteLikesByTargetIdIn(Set<String> targetIds) {
        log.info("Deleting likes with targetIds: {}",targetIds);
        repository.deleteAllByTargetIdIn(targetIds);
    }

    @Override
    @Transactional
    public void deleteUserLikes(String userId) {
        log.info("Deleting user likes: {}",userId);
        repository.deleteAllByUserId(userId);
    }
}
