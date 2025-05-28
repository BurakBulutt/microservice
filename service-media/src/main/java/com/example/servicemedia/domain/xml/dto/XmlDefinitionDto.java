package com.example.servicemedia.domain.xml.dto;

import com.example.servicemedia.domain.xml.enums.DefinitionType;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class XmlDefinitionDto {
    private String id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String fileName;
    private DefinitionType type;
    private byte[] xmlFile;
    private Boolean success;
    private String errorMessage;
    private String JobExecutionId;
}
