package com.example.servicemedia.xml.api;

import com.example.servicemedia.xml.enums.DefinitionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XmlDefinitionResponse {
    private String fileName;
    private DefinitionType type;
    private Boolean success;
    private String errorMessage;
}
