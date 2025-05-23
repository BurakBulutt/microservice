package com.example.servicereaction.domain.like.repo;

import com.example.servicereaction.domain.like.enums.LikeType;
import com.example.servicereaction.domain.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, String> {
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetId = :targetId AND l.likeType = :likeType")
    Integer findTargetLikeCount(@Param("targetId") String targetId,@Param("likeType")  LikeType likeType);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END "
            + "FROM Like l "
            + "WHERE l.targetId = :targetId AND l.userId = :userId AND l.likeType = :likeType")
    Boolean isUserLiked(@Param("targetId") String targetId,@Param("userId")  String userId,@Param("likeType")  LikeType likeType);

    void deleteAllByTargetId(String targetId);
    void deleteAllByTargetIdIn(Set<String> targetIds);
    void deleteAllByUserId(String userId);

    Optional<Like> findByTargetIdAndUserIdAndLikeType(String targetId, String userId, LikeType likeType);

    @Query(value = "SELECT l.target_id FROM likes l WHERE l.like_target = 'CONTENT' AND l.like_type = :likeType GROUP BY l.target_id ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopContentLikeTarget(@Param("likeType") String likeType);

}
