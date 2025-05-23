package com.example.servicereaction.domain.comment.repo;

import com.example.servicereaction.domain.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment,String>, JpaSpecificationExecutor<Comment> {
    Page<Comment> findAllByTargetId(Pageable pageable,String targetId);
    List<Comment> findAllByTargetIdIn(Set<String> targetIds);
    List<Comment> findAllByUserId(String userId);
}
