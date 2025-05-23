package com.example.servicemedia.domain.content.repo;

import com.example.servicemedia.domain.content.model.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, String>, JpaSpecificationExecutor<Content> {
    Optional<Content> findBySlug(String slug);
}
