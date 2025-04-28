package com.example.servicemedia.xml.event;

import com.example.servicemedia.xml.model.XmlDefinition;
import com.example.servicemedia.xml.service.XmlDefinitionService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class XmlDefinitionListener {
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    private final Job importXmlJob;

    private final XmlDefinitionService xmlDefinitionService;

    @EventListener(value = StartJobEvent.class)
    public void startJobEvent(StartJobEvent event) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addString("definitionId", event.definitionId())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importXmlJob, parameters);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void restartFailedJobs() {
        log.info("Starting to check for FAILED jobs to restart...");
        final String jobName = "importXmlJob";
        List<JobExecution> executions = jobExplorer.findRunningJobExecutions(jobName)
                .stream()
                .filter(execution -> execution.getStatus() == BatchStatus.FAILED)
                .toList();

        for (JobExecution execution : executions) {
            String definitionId = execution.getJobParameters().getString("definitionId");
            XmlDefinition definition = xmlDefinitionService.getById(definitionId);
            if (!definition.getSuccess() && definition.getErrorMessage() != null) {
                continue;
            }
            try {
                log.info("Restarting failed job {} with executionId {}", jobName, execution.getId());
                jobOperator.restart(execution.getId());
            } catch (Exception e) {
                log.error("Failed to restart job {} with executionId {}. Error: {}", jobName, execution.getId(), e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
