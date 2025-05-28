package com.example.servicemedia.domain.xml.elasticsearch.event;

import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;

public record CreateXmlDefinitionEvent(
        XmlDefinitionDto xmlDefinition
) {
}
