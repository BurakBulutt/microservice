package com.example.servicereaction.comment.repo;

import com.example.servicereaction.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,String> {
    List<Comment> findAllByParentId(String parentId);
    Page<Comment> findAllByTargetIdAndParentNull(String targetId, Pageable pageable);
}
