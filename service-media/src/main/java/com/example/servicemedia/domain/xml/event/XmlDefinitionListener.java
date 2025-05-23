package com.example.servicemedia.domain.xml.event;

import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.servicemedia.domain.xml.constants.XmlConstants.BATCH_DEFINITION_ID;
import static com.example.servicemedia.domain.xml.constants.XmlConstants.BATCH_IMPORT_XML_JOB;

@Component
@RequiredArgsConstructor
@Slf4j
public class XmlDefinitionListener {
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    private final Job importXmlJob;

    @Async
    @EventListener(value = StartJobEvent.class)
    public void startJobEvent(StartJobEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addString(BATCH_DEFINITION_ID, event.definitionId())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importXmlJob, parameters);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void restartFailedJobs() {
        log.info("Starting to check for FAILED jobs to restart...");
        List<JobExecution> executions = jobExplorer.findRunningJobExecutions(BATCH_IMPORT_XML_JOB)
                .stream()
                .filter(execution -> execution.getStatus() == BatchStatus.FAILED)
                .toList();

        for (JobExecution execution : executions) {
            try {
                log.info("Restarting failed job {} with executionId {}", BATCH_IMPORT_XML_JOB, execution.getId());
                jobOperator.restart(execution.getId());
            } catch (Exception e) {
                log.error("Failed to restart job {} with executionId {}. Error: {}", BATCH_IMPORT_XML_JOB, execution.getId(), e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
