package com.example.servicemedia.domain.xml.elasticsearch.event;

import com.example.servicemedia.domain.xml.model.XmlDefinition;

public record SaveXmlDefinitionEvent(
        XmlDefinition xmlDefinition
) {
}
