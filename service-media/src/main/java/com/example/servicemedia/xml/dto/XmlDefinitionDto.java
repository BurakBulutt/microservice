package com.example.servicemedia.xml.dto;

import com.example.servicemedia.xml.enums.DefinitionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XmlDefinitionDto {
    private String id;
    private String fileName;
    private DefinitionType type;
    private byte[] xmlFile;
    private Boolean success;
    private String errorMessage;
}
