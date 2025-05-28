package com.example.servicemedia.domain.xml.mapper;

import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlDefinitionServiceMapper {

    public static XmlDefinitionDto toDto(XmlDefinition xmlDefinition) {
        return XmlDefinitionDto.builder()
                .id(xmlDefinition.getId())
                .created(xmlDefinition.getCreated())
                .modified(xmlDefinition.getModified())
                .fileName(xmlDefinition.getFileName())
                .xmlFile(xmlDefinition.getXmlFile())
                .success(xmlDefinition.getSuccess())
                .type(xmlDefinition.getType())
                .errorMessage(xmlDefinition.getErrorMessage())
                .JobExecutionId(xmlDefinition.getJobExecutionId())
                .build();
    }

}
