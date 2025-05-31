package com.example.servicereaction.domain.comment.mapper;

import com.example.servicereaction.domain.comment.api.AddCommentRequest;
import com.example.servicereaction.domain.comment.api.CommentResponse;
import com.example.servicereaction.domain.comment.api.UpdateCommentRequest;
import com.example.servicereaction.domain.comment.dto.CommentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentApiMapper {

    public static CommentResponse toResponse(CommentDto commentDto) {
        return CommentResponse.builder()
                .id(commentDto.getId())
                .content(commentDto.getContent())
                .commentType(commentDto.getCommentType())
                .targetType(commentDto.getTargetType())
                .commentList(commentDto.getCommentList())
                .parent(commentDto.getParent())
                .likeCount(commentDto.getLikeCount())
                .user(commentDto.getUser())
                .target(commentDto.getTarget())
                .build();
    }

    public static CommentDto toDto(AddCommentRequest request) {
        return CommentDto.builder()
                .content(request.content())
                .targetType(request.targetType())
                .commentType(request.type())
                .parent(CommentDto.builder().id(request.parentId()).build())
                .userId(request.userId())
                .targetId(request.targetId())
                .build();
    }

    public static CommentDto toDto(UpdateCommentRequest request) {
        return CommentDto.builder()
                .content(request.content())
                .build();
    }

    public static Page<CommentResponse> toPageResponse(Page<CommentDto> dtoPage){
        return dtoPage.map(CommentApiMapper::toResponse);
    }
}
