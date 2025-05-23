package com.example.servicereaction.deadletterqueue.service;

import com.example.servicereaction.deadletterqueue.dto.DeadLetterQueueDto;
import com.example.servicereaction.deadletterqueue.enums.MessageType;
import com.example.servicereaction.deadletterqueue.mapper.DeadLetterQueueServiceMapper;
import com.example.servicereaction.deadletterqueue.model.DeadLetterQueue;
import com.example.servicereaction.deadletterqueue.repo.DeadLetterQueueRepository;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
    public void delete(ObjectId id) {
        DeadLetterQueue deadLetterQueue = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND,id.toString()));

        log.warn("Deleting dlq message: {}",id);
        repository.delete(deadLetterQueue);
    }
}
