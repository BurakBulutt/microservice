package com.example.servicemedia.xml.api;

import com.example.servicemedia.xml.enums.DefinitionType;

public record XmlDefinitionRequest(
        DefinitionType type,
        String base64
) {
}
