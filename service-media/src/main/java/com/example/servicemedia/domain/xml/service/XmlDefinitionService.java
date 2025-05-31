package com.example.servicemedia.domain.xml.service;

import com.example.servicemedia.domain.xml.api.XmlDefinitionRequest;
import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface XmlDefinitionService {
    Page<XmlDefinitionDto> getAll(Pageable pageable);
    Page<XmlDefinitionDto> filter(Pageable pageable, String query);

    XmlDefinition getById(String id);

    void save(XmlDefinitionRequest request) throws IOException;
    void update(XmlDefinition definition);
    void delete(String id);

    void startJob(String definitionId);
}
