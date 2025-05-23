package com.example.servicemedia.domain.media.repo;

import com.example.servicemedia.domain.media.model.MediaSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaSourceRepository extends JpaRepository<MediaSource,String> {
    List<MediaSource> findAllByMediaIdAndFansubId(String mediaId, String fansubId);
    List<MediaSource> findAllByMediaId(String mediaId);
    void deleteMediaSourcesByMediaId(String mediaId);

}
