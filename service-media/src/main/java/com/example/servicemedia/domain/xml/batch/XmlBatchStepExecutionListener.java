package com.example.servicemedia.domain.xml.batch;

import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import static com.example.servicemedia.domain.xml.constants.XmlConstants.BATCH_DEFINITION_ID;

@Component
@RequiredArgsConstructor
public class XmlBatchStepExecutionListener implements StepExecutionListener {
    private final XmlDefinitionService service;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
            XmlDefinition definition = service.getById(stepExecution.getJobExecution().getJobParameters().getString(BATCH_DEFINITION_ID));
            definition.setSuccess(Boolean.FALSE);
            definition.setErrorMessage(stepExecution.getExitStatus().getExitDescription().substring(0,2500));
            service.update(definition);
        }
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
