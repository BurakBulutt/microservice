package com.example.servicemedia.media.repo;

import com.example.servicemedia.media.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media,String> {
    @Query("SELECT m FROM Media m WHERE m.contentId = :contentId ORDER BY m.count ASC")
    Page<Media> findAllByContentId(@Param("contentId") String contentId, Pageable pageable); //TODO WILL DEPRECATE

    List<Media> findAllByContentId(String contentId);//TODO FE DE BUNU KULLANCAZ

    @Query(value = "SELECT * FROM media m WHERE m.created >= CURRENT_DATE - INTERVAL '30 days'",
            countQuery = "SELECT count(*) FROM media m WHERE m.created >= CURRENT_DATE - INTERVAL '30 days'",
            nativeQuery = true)
    Page<Media> findNewMedias(Pageable pageable);


    Optional<Media> findBySlug(String slug);

    void deleteAllByContentId(String contentId);
}
