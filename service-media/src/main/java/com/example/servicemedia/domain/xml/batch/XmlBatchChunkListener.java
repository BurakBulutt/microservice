package com.example.servicemedia.domain.xml.batch;

import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.servicemedia.domain.xml.constants.XmlConstants.BATCH_DEFINITION_ID;

@Component
@RequiredArgsConstructor
public class XmlBatchChunkListener implements ChunkListener {
    private final XmlDefinitionService service;

    @Override
    public void beforeChunk(@NonNull ChunkContext context) {
        XmlDefinition definition = getDefinition(context);

        definition.setJobExecutionId(String.valueOf(context.getStepContext().getStepExecution().getJobExecutionId()));
        definition.setSuccess(Boolean.FALSE);
        service.update(definition);

        context.setAttribute("file",definition.getXmlFile());
        context.setAttribute("type",definition.getType());

        ChunkListener.super.beforeChunk(context);
    }

    @Override
    public void afterChunk(@NonNull ChunkContext context) {
        XmlDefinition definition = getDefinition(context);

        definition.setSuccess(Boolean.TRUE);
        service.update(definition);

        ChunkListener.super.afterChunk(context);
    }

    @Override
    public void afterChunkError(@NonNull ChunkContext context) {
        List<Throwable> exceptions = context.getStepContext().getStepExecution().getFailureExceptions();

        XmlDefinition definition = getDefinition(context);

        definition.setSuccess(Boolean.FALSE);
        definition.setErrorMessage(exceptions.get(exceptions.size()-1).getLocalizedMessage());
        service.update(definition);

        ChunkListener.super.afterChunkError(context);
    }

    private XmlDefinition getDefinition(ChunkContext context) {
        final String definitionId = (String) context.getStepContext().getJobParameters().get(BATCH_DEFINITION_ID);
        return service.getById(definitionId);
    }
}
