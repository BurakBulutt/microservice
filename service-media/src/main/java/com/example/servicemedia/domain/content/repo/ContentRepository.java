package com.example.servicemedia.domain.content.repo;

import com.example.servicemedia.domain.content.model.Content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.Set;

public interface ContentRepository extends JpaRepository<Content, String>, JpaSpecificationExecutor<Content> {
    Optional<Content> findBySlug(String slug);
    Page<Content> findAllByIdIn(Pageable pageable, Set<String> ids);
}
