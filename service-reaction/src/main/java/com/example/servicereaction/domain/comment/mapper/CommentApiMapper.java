package com.example.servicereaction.domain.comment.mapper;

import com.example.servicereaction.domain.comment.api.AddCommentRequest;
import com.example.servicereaction.domain.comment.api.CommentResponse;
import com.example.servicereaction.domain.comment.api.UpdateCommentRequest;
import com.example.servicereaction.feign.user.UserResponse;
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
                .user(commentDto.getUser())
                .type(commentDto.getType())
                .targetId(commentDto.getTargetId())
                .commentList(commentDto.getCommentList())
                .parent(commentDto.getParent())
                .likeCount(commentDto.getLikeCount())
                .build();
    }

    public static CommentDto toDto(AddCommentRequest request) {
        return CommentDto.builder()
                .content(request.content())
                .targetId(request.targetId())
                .type(request.type())
                .parent(CommentDto.builder().id(request.parentId()).build())
                .user(UserResponse.builder().id(request.userId()).build())
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
