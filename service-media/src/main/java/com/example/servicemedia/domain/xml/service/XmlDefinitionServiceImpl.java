package com.example.servicemedia.domain.xml.service;

import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import com.example.servicemedia.domain.xml.api.XmlDefinitionRequest;
import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.domain.xml.event.StartJobEvent;
import com.example.servicemedia.domain.xml.mapper.XmlDefinitionServiceMapper;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.repo.XmlDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlDefinitionServiceImpl implements XmlDefinitionService {
    private final ApplicationEventPublisher eventPublisher;
    private final XmlDefinitionRepository repository;
    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    @Override
    @Transactional(readOnly = true)
    public Page<XmlDefinitionDto> getAll(Pageable pageable) {
        log.info("Getting all xml definitions");
        return repository.findAll(pageable).map(XmlDefinitionServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public XmlDefinition getById(String id) {
        log.info("Getting xml definition by id : {}", id);
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(XmlDefinitionRequest request) throws IOException {
        byte[] file = Base64.getDecoder().decode(request.base64());

        if (!isValidXml(file)) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }

        XmlDefinition xmlDefinition = new XmlDefinition();
        xmlDefinition.setXmlFile(file);
        xmlDefinition.setType(request.type());
        xmlDefinition.setFileName(UUID.randomUUID().toString());

        log.info("Saving xml definition : {}", xmlDefinition);
        repository.save(xmlDefinition);

        final String definitionId = xmlDefinition.getId();
        TransactionSynchronization synchronization = new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("Publishing start job event : {}", definitionId);
                eventPublisher.publishEvent(new StartJobEvent(definitionId));
                TransactionSynchronization.super.afterCommit();
            }
        };
        TransactionSynchronizationManager.registerSynchronization(synchronization);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(XmlDefinition definition) {
        log.info("Updating xml definition : {}", definition);
        repository.save(definition);
    }

    @Override
    @Transactional
    public void delete(String id) {
        XmlDefinition xmlDefinition = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
        JobExecution execution = jobExplorer.getJobExecution(Long.parseLong(xmlDefinition.getJobExecutionId()));
        if (execution != null) {
            JobInstance instance = execution.getJobInstance();
            log.warn("Deleting job instance : {}", instance);
            jobRepository.deleteJobInstance(instance);
        }

        log.warn("Deleting xml definition : {}", xmlDefinition);
        repository.delete(xmlDefinition);
    }

    @Override
    @Transactional(readOnly = true,propagation = Propagation.NEVER)
    public void startJob(String id) {
        XmlDefinition xmlDefinition = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
        long executionId = Long.parseLong(xmlDefinition.getJobExecutionId());
        try {
            log.info("Restarting job with executionId {} for xml definition : {}", executionId,xmlDefinition);
            jobOperator.restart(executionId);
        } catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException |
                 JobRestartException | JobParametersInvalidException e) {
            log.error("Failed to restart job with executionId {}. Error: {}", executionId, e.getLocalizedMessage());
        }
    }

    private boolean isValidXml(byte[] xmlContent) {
        final String xmlSignature = "<?xml";
        final String header = new String(xmlContent, 0, Math.min(5, xmlContent.length));
        return header.startsWith(xmlSignature);
    }
}
