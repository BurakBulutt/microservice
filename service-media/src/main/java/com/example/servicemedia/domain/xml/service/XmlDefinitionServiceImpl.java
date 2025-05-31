package com.example.servicemedia.domain.xml.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.servicemedia.util.CreatorComponent.fullTextSearchQuery;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class XmlDefinitionServiceImpl implements XmlDefinitionService {
    private final ApplicationEventPublisher publisher;
    private final XmlDefinitionRepository repository;
    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<XmlDefinitionDto> getAll(Pageable pageable) {
        log.info("Getting all xml definitions");
        return repository.findAll(pageable).map(XmlDefinitionServiceMapper::toDto);
    }

    @Override
    public Page<XmlDefinitionDto> filter(Pageable pageable, String query) {
        log.info("Getting filtered xml definitions: [query: {}]",query);

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (query != null && query.length() >= 2) {
            queryBuilder.must(fullTextSearchQuery(query));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticXmlDefinition> search = elasticsearchOperations.search(nativeQuery, ElasticXmlDefinition.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return new PageImpl<>(repository.findAllByIdIn(ids,nativeQuery.getSort()),pageable,search.getTotalHits()).map(XmlDefinitionServiceMapper::toDto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        xmlDefinition.setFileName(StringUtils.hasLength(request.fileName()) ? request.fileName() : UUID.randomUUID().toString());

        log.info("Saving xml definition : {}", xmlDefinition);
        repository.save(xmlDefinition);

        final String definitionId = xmlDefinition.getId();
        TransactionSynchronization synchronization = new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("Publishing start job event : {}", definitionId);
                publisher.publishEvent(new StartJobEvent(definitionId));
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
        JobExecution execution = jobExplorer.getJobExecution(xmlDefinition.getJobExecutionId());
        if (execution != null) {
            JobInstance instance = execution.getJobInstance();
            log.warn("Deleting job instance : {}", instance);
            jobRepository.deleteJobInstance(instance);
        }

        log.warn("Deleting xml definition : {}", xmlDefinition);
        repository.delete(xmlDefinition);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void startJob(String id) {
        XmlDefinition xmlDefinition = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
        try {
            log.info("Restarting job with executionId {} for xml definition : {}", xmlDefinition.getJobExecutionId(),xmlDefinition);
            jobOperator.restart(xmlDefinition.getJobExecutionId());
        } catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException |
                 JobRestartException | JobParametersInvalidException e) {
            log.error("Failed to restart job with executionId {}. Error: {}", xmlDefinition.getJobExecutionId(), e.getLocalizedMessage());
        }
    }

    private boolean isValidXml(byte[] xmlContent) {
        final String xmlSignature = "<?xml";
        final String header = new String(xmlContent, 0, Math.min(5, xmlContent.length));
        return header.startsWith(xmlSignature);
    }
}
