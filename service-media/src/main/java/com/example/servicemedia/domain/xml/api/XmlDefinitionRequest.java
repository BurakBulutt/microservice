package com.example.servicemedia.domain.xml.api;

import com.example.servicemedia.domain.xml.enums.DefinitionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record XmlDefinitionRequest(
        @NotNull(message = "validation.xmlDefinition.type.notNull")
        DefinitionType type,
        @NotNull(message = "validation.xmlDefinition.base64.notNull")
        @NotBlank(message = "validation.xmlDefinition.base64.notBlank")
        String base64,
        String fileName
) {
}
