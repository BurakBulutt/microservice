package com.example.servicemedia.xml.mapper;

import com.example.servicemedia.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.xml.model.XmlDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlDefinitionServiceMapper {

    public static XmlDefinitionDto toDto(XmlDefinition xmlDefinition) {
        return XmlDefinitionDto.builder()
                .fileName(xmlDefinition.getFileName())
                .xmlFile(xmlDefinition.getXmlFile())
                .success(xmlDefinition.getSuccess())
                .type(xmlDefinition.getType())
                .errorMessage(xmlDefinition.getErrorMessage())
                .build();
    }

}
