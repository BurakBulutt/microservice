package com.example.servicemedia.media.repo;

import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.media.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,String>, JpaSpecificationExecutor<Media> {
    Optional<Media> findBySlug(String slug);
}
