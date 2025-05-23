package com.example.servicemedia.domain.fansub.service;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.mapper.FansubServiceMapper;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.repo.FansubRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
public class FansubServiceImpl implements FansubService {
    private final FansubRepository repository;

    @Override
    public Page<FansubDto> getAll(Pageable pageable) {
        log.info("Getting all fansubs");
        return repository.findAll(pageable).map(FansubServiceMapper::toDto);
    }

    @Override
    public Page<FansubDto> filter(Pageable pageable) {
        log.info("Getting filtered fansubs");
        return repository.findAll(pageable).map(FansubServiceMapper::toDto);
    }

    @Override
    public Long count() {
        log.info("Getting fansubs count");
        return repository.count();
    }

    @Override
    public FansubDto getById(String id) {
        log.info("Getting fansub by id: {}", id);
        return repository.findById(id).map(FansubServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));
    }

    @Override
    public Fansub findByName(String name) {
        log.info("Getting fansub by name: {}", name);
        return repository.findByName(name).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), name));
    }

    @Override
    @Transactional
    public Fansub findOrCreateByName(String name) {
        log.info("Getting fansub by name: {}, if not found, it will be created with name", name);
        return repository.findByName(name).orElse(repository.save(new Fansub(name,null, Collections.emptyList())));
    }

    @Override
    @Transactional
    public FansubDto save(FansubDto fanSubDto) {
        log.info("Saving fansub: {}", fanSubDto);
        return FansubServiceMapper.toDto(repository.save(FansubServiceMapper.toEntity(new Fansub(),fanSubDto)));
    }

    @Override
    @Transactional
    public FansubDto update(String id, FansubDto fanSubDto) {
        Fansub fanSub = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));

        log.info("Updating fansub: {}, updated: {}",id,fanSubDto);
        return FansubServiceMapper.toDto(repository.save(FansubServiceMapper.toEntity(fanSub,fanSubDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Fansub fanSub = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));

        log.warn("Deleting fansub: {}, updated: {}",id,fanSub);
        repository.delete(fanSub);
    }
}
