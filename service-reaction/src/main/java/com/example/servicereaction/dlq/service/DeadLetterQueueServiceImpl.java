package com.example.servicereaction.dlq.service;

import com.example.servicereaction.dlq.dto.DeadLetterQueueDto;
import com.example.servicereaction.dlq.enums.MessageType;
import com.example.servicereaction.dlq.mapper.DeadLetterQueueServiceMapper;
import com.example.servicereaction.dlq.model.DeadLetterQueue;
import com.example.servicereaction.dlq.repo.DeadLetterQueueRepository;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueServiceImpl implements DeadLetterQueueService {
    private final DeadLetterQueueRepository repository;

    @Override
    public Page<DeadLetterQueueDto> getAll(Pageable pageable) {
        log.info("Getting all dlq messages");
        return repository.findAll(pageable).map(DeadLetterQueueServiceMapper::toDto);
    }

    @Override
    public Page<DeadLetterQueueDto> getAllByType(MessageType type, Pageable pageable) {
        log.info("Getting all dlq messages by type : {}",type);
        return repository.findAllByType(type,pageable).map(DeadLetterQueueServiceMapper::toDto);
    }

    @Override
    public void save(DeadLetterQueueDto deadLetterQueueDto) {
        log.info("Saving dlq message: {}",deadLetterQueueDto);
        repository.save(DeadLetterQueueServiceMapper.toEntity(new DeadLetterQueue(),deadLetterQueueDto));
    }

    @Override
    public void delete(String id) {
        DeadLetterQueue dlq = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,id));

        log.warn("Deleting dlq message: {}",id);
        repository.delete(dlq);
    }
}
