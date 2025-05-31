package com.example.servicemedia.domain.xml.batch;

import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XmlBatchExceptionHandler implements ExceptionHandler {
    private final XmlDefinitionService xmlDefinitionService;

    @Override
    public void handleException(RepeatContext context, Throwable throwable) throws Throwable {
        String id = (String) context.getAttribute("id");
        XmlDefinition definition = xmlDefinitionService.getById(id);
        definition.setSuccess(Boolean.FALSE);
        definition.setErrorMessage(throwable.getLocalizedMessage());
        xmlDefinitionService.update(definition);
        context.close();

        throw throwable;
    }
}
