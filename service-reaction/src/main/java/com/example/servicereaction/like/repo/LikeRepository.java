package com.example.servicereaction.like.repo;

import com.example.servicereaction.like.enums.LikeType;
import com.example.servicereaction.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, String> {
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetId = :targetId AND l.likeType = :likeType")
    Integer findTargetLikeCount(@Param("targetId") String targetId,@Param("likeType")  LikeType likeType);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Like l "
            + "WHERE l.targetId = :targetId AND l.userId = :userId AND l.likeType = :likeType")
    Boolean isUserLiked(@Param("targetId") String targetId,@Param("userId")  String userId,@Param("likeType")  LikeType likeType);
}
