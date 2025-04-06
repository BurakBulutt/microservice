package com.example.servicereaction.comment.repo;

import com.example.servicereaction.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment,String> {
    Page<Comment> findAllByTargetIdAndParentNull(String targetId, Pageable pageable);
    List<Comment> findAllByTargetIdAndParentNull(String targetId);
    List<Comment> findAllByTargetIdInAndParentNull(Set<String> targetIds);
    List<Comment> findAllByUserIdAndParentNull(String userId);
}
