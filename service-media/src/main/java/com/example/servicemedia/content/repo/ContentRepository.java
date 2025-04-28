package com.example.servicemedia.content.repo;

import com.example.servicemedia.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, String> {
    Optional<Content> findBySlug(String slug);

    @Query(
            value = "SELECT DISTINCT c.* FROM content c " +
                    "INNER JOIN category_content cc ON c.id = cc.content_id " +
                    "WHERE (:categoryId IS NULL OR cc.category_id = :categoryId) " +
                    "AND (:name IS NULL OR UPPER(c.name) LIKE UPPER(CONCAT('%',:name,'%')))" +
                    "ORDER BY c.id ASC",
            countQuery = "SELECT COUNT(DISTINCT c.id) FROM content c " +
                    "INNER JOIN category_content cc ON c.id = cc.content_id " +
                    "WHERE (:categoryId IS NULL OR cc.category_id = :categoryId) " +
                    "AND (:name IS NULL OR UPPER(c.name) LIKE UPPER(CONCAT('%',:name,'%')))",
            nativeQuery = true)
    Page<Content> filter(@Param("name") String name, @Param("categoryId") String categoryId, Pageable pageable);

}
