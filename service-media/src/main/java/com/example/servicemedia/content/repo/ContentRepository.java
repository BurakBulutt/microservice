package com.example.servicemedia.content.repo;

import com.example.servicemedia.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content,String> {
    Optional<Content> findBySlug(String slug);

    @Query(
            value = "SELECT * FROM content WHERE start_date >= CURRENT_DATE - INTERVAL '90 days'",
            countQuery = "SELECT * FROM content WHERE start_date >= CURRENT_DATE - INTERVAL '90 days'",
            nativeQuery = true)
    Page<Content> findNewContents(Pageable pageable);

    @Query(
            value = "SELECT * FROM content c INNER JOIN category_content cc ON c.id = cc.content_id WHERE cc.category_id = :categoryId",
            countQuery = "SELECT count(*) FROM content c INNER JOIN category_content cc ON c.id = cc.content_id WHERE cc.category_id = :categoryId",
            nativeQuery = true)
    Page<Content> findAllByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    Page<Content> findAllByNameContainsIgnoreCase(String name, Pageable pageable);
}
