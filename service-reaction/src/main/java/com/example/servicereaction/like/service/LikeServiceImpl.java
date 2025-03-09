package com.example.servicereaction.like.service;

import com.example.servicereaction.like.dto.LikeCountDto;
import com.example.servicereaction.like.dto.LikeDto;
import com.example.servicereaction.like.enums.LikeType;
import com.example.servicereaction.like.mapper.LikeServiceMapper;
import com.example.servicereaction.like.model.Like;
import com.example.servicereaction.like.repo.LikeRepository;
import com.example.servicereaction.util.rest.BaseException;
import com.example.servicereaction.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {
    private final LikeRepository repository;

    @Override
    public LikeCountDto findLikeCount(String targetId, String userId) {
        Integer likeCount = repository.findTargetLikeCount(targetId,LikeType.LIKE);
        Integer dislikeCount = repository.findTargetLikeCount(targetId,LikeType.DISLIKE);
        Boolean isUserLiked = false;
        Boolean isUserDisliked = false;
        if (userId != null) {
            isUserLiked = repository.isUserLiked(targetId, userId, LikeType.LIKE);
            isUserDisliked = repository.isUserLiked(targetId, userId, LikeType.DISLIKE);
        }
        return LikeCountDto.builder()
                .targetId(targetId)
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .isUserLiked(isUserLiked)
                .isUserDisliked(isUserDisliked)
                .build();
    }

    @Override
    @Transactional
    public void save(LikeDto likeDto) {
        repository.save(LikeServiceMapper.toEntity(new Like(), likeDto));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Like like = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Like.class.getSimpleName(), id));
        repository.delete(like);
    }
}
