package com.example.servicereaction.domain.comment.repo;

import com.example.servicereaction.domain.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment,String>, JpaSpecificationExecutor<Comment> {
    List<Comment> findAllByIdIn(Collection<String> ids, Sort sort);
    List<Comment> findAllByTargetIdIn(Set<String> targetIds);
    List<Comment> findAllByUserId(String userId);

    @Query(
            value = "select c from Comment c where c.targetId = :targetId and c.parent is null",
            countQuery = "select count(c) from Comment c where c.targetId = :targetId and c.parent is null"
    )
    Page<Comment> findAllByTargetIdAndParentIsNull(Pageable pageable,@Param("targetId") String targetId);

    Page<Comment> findAllByParentIsNull(Pageable pageable);
}
