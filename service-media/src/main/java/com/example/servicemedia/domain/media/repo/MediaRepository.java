package com.example.servicemedia.domain.media.repo;

import com.example.servicemedia.domain.media.model.Media;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,String>, JpaSpecificationExecutor<Media> {
    Optional<Media> findBySlug(String slug);

    List<Media> findAllByIdIn(Collection<String> ids, Sort sort);
}
