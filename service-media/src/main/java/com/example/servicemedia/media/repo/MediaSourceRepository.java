package com.example.servicemedia.media.repo;

import com.example.servicemedia.media.model.MediaSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaSourceRepository extends JpaRepository<MediaSource,String> {
    List<MediaSource> findAllByMediaId(String mediaId);

    void deleteMediaSourcesByMediaId(String mediaId);
}
