package com.example.servicemedia.domain.xml.batch;

import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.service.ContentService;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.service.MediaService;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.domain.xml.factory.DefinitionConverterFactory;
import com.example.servicemedia.domain.xml.converter.ContentDefinitionConverter;
import com.example.servicemedia.domain.xml.converter.MediaDefinitionConverter;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.domain.xml.service.XmlDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.servicemedia.domain.xml.constants.XmlConstants.*;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class XmlBatch {
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    private final XmlDefinitionService xmlDefinitionService;
    private final ContentService contentService;
    private final MediaService mediaService;
    private final MessageSource messageSource;

    @Bean
    public Job importXmlJob(Step step) {
        return new JobBuilder(BATCH_IMPORT_XML_JOB, jobRepository)
                .start(step)
                .build();
    }

    @Bean(name = BATCH_IMPORT_XML_STEP)
    public Step importXmlStep(Tasklet importXmlTask) {
        return new StepBuilder(BATCH_IMPORT_XML_STEP, jobRepository)
                .tasklet(importXmlTask, transactionManager)
                .build();
    }

    @Bean
    public Tasklet importXmlTask() {
        return (contribution, chunkContext) -> {
            final String definitionId = (String) chunkContext.getStepContext().getJobParameters().get(BATCH_DEFINITION_ID);

            XmlDefinition definition = xmlDefinitionService.getById(definitionId);
            definition.setJobExecutionId(String.valueOf(contribution.getStepExecution().getJobExecutionId()));

            try {
                byte[] file = definition.getXmlFile();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(file);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);
                document.getDocumentElement().normalize();

                NodeList nodeList = document.getElementsByTagName(BATCH_DEFINITION);

                switch (definition.getType()) {
                    case CONTENT -> {
                        ContentDefinitionConverter contentStrategy = DefinitionConverterFactory.getContentConverter();
                        processXml(nodeList, contentStrategy);
                    }
                    case MEDIA -> {
                        MediaDefinitionConverter mediaStrategy = DefinitionConverterFactory.getMediaConverter();
                        processXml(nodeList, mediaStrategy);
                    }
                    default -> throw new IllegalArgumentException("Unsupported definition type: " + definition.getType());
                }
                inputStream.close();
                definition.setSuccess(Boolean.TRUE);
            } catch (Exception e) {
                final String msg;
                if (e instanceof BaseException) {
                    msg = messageSource.getMessage(((BaseException) e).getMessageResource().getMessage(),((BaseException) e).getArgs(), Locale.getDefault());
                }else {
                    msg = e.getLocalizedMessage();
                }
                definition.setSuccess(Boolean.FALSE);
                definition.setErrorMessage(msg);
                log.error("Task Failed: {}", msg);
                throw new RuntimeException(e);
            } finally {
                xmlDefinitionService.update(definition);
            }
            return RepeatStatus.FINISHED;
        };
    }

    private void processXml(NodeList nodeList, ContentDefinitionConverter converter) {
        List<ContentDto> contentList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element nodeElement = (Element) node;

            ContentDto content = converter.convert(nodeElement);
            contentList.add(content);
        }

        contentService.saveContentsBulk(contentList);
    }

    private void processXml(NodeList nodeList, MediaDefinitionConverter converter) {
        List<MediaDto> mediaList = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element nodeElement = (Element) node;

            MediaDto content = converter.convert(nodeElement);
            mediaList.add(content);
        }

        mediaService.saveMediasBulk(mediaList);
    }
}
