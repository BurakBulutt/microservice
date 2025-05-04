package com.example.servicemedia.xml.service;

import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import com.example.servicemedia.xml.api.XmlDefinitionRequest;
import com.example.servicemedia.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.xml.event.StartJobEvent;
import com.example.servicemedia.xml.mapper.XmlDefinitionServiceMapper;
import com.example.servicemedia.xml.model.XmlDefinition;
import com.example.servicemedia.xml.repo.XmlDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class XmlDefinitionServiceImpl implements XmlDefinitionService {
    private final ApplicationEventPublisher eventPublisher;
    private final XmlDefinitionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<XmlDefinitionDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(XmlDefinitionServiceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public XmlDefinition getById(String id) {
        return repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
    }

    @Override
    public void save(XmlDefinitionRequest request) throws IOException {
        byte[] file = Base64.getDecoder().decode(request.base64());

        if (!isValidXml(file)) {
            throw new BaseException(MessageResource.BAD_REQUEST);
        }

        XmlDefinition xmlDefinition = new XmlDefinition();
        xmlDefinition.setXmlFile(file);
        xmlDefinition.setType(request.type());
        xmlDefinition.setFileName(UUID.randomUUID().toString());

        xmlDefinition = repository.save(xmlDefinition);

        eventPublisher.publishEvent(new StartJobEvent(xmlDefinition.getId()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(XmlDefinition definition) {
        repository.save(definition);
    }

    @Override
    @Transactional
    public void delete(String id) {
        XmlDefinition xmlDefinition = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
        repository.delete(xmlDefinition);
    }

    @Override
    @Transactional(readOnly = true)
    public void startJob(String id) {
        XmlDefinition xmlDefinition = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, XmlDefinition.class.getSimpleName(), id));
        eventPublisher.publishEvent(new StartJobEvent(xmlDefinition.getId()));
    }

    private boolean isValidXml(byte[] xmlContent) {
        final String xmlSignature = "<?xml";
        final String header = new String(xmlContent, 0, Math.min(5, xmlContent.length));
        return header.startsWith(xmlSignature);
    }
}
