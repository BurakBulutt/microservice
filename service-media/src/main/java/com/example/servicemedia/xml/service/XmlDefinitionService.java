package com.example.servicemedia.xml.service;

import com.example.servicemedia.xml.api.XmlDefinitionRequest;
import com.example.servicemedia.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.xml.model.XmlDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface XmlDefinitionService {
    Page<XmlDefinitionDto> getAll(Pageable pageable);
    XmlDefinition getById(String id);

    void save(XmlDefinitionRequest request) throws IOException;
    void update(XmlDefinition definition);
    void delete(String id);

    void startJob(String definitionId);
}
