package com.example.servicereaction.comment.repo;

import com.example.servicereaction.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment,String> {
    Page<Comment> findAllByTargetId(String targetId,Pageable pageable);
    Page<Comment> findAllByTargetIdAndParentNull(String targetId, Pageable pageable);
    List<Comment> findAllByTargetId(String targetId);
    List<Comment> findAllByTargetIdIn(Set<String> targetIds);
    List<Comment> findAllByUserId(String userId);
}
