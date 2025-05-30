package com.example.servicemedia.domain.fansub.service;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.model.Fansub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FansubService {
    Page<FansubDto> getAll(Pageable pageable);
    Page<FansubDto> filter(Pageable pageable,String query);

    Long count();

    FansubDto getById(String id);

    Fansub findOrCreateByName(String name);

    FansubDto save(FansubDto fanSubDto);
    FansubDto update(String id, FansubDto fanSubDto);
    void delete(String id);
}
