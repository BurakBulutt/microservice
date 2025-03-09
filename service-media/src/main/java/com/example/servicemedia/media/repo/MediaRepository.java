package com.example.servicemedia.media.repo;

import com.example.servicemedia.media.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,String> {
    @Query("SELECT m FROM Media m WHERE m.contentId = :contentId ORDER BY m.count ASC")
    Page<Media> findAllByContentId(@Param("contentId") String contentId, Pageable pageable);

    List<Media> findAllByContentId(String contentId);

    Page<Media> findAllByPublishDateIsAfter(Date publishDateAfter, Pageable pageable);

    @Query(value = "SELECT * FROM media WHERE created >= CURRENT_DATE - INTERVAL '30 days'",nativeQuery = true)
    Page<Media> findNewMedias(Pageable pageable);

    Optional<Media> findBySlug(String slug);

    void deleteAllByContentId(String contentId);
}
