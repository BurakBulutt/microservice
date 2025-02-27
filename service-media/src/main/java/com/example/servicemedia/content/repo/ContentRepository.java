package com.example.servicemedia.content.repo;

import com.example.servicemedia.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content,String> {
    Optional<Content> findBySlug(String slug);

    @Query(value = "SELECT * FROM content WHERE start_date >= CURRENT_DATE - INTERVAL '90 days'",nativeQuery = true)
    Page<Content> findNewContents(Pageable pageable);
}
