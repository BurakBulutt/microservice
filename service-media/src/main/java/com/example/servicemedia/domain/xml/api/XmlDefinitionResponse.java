package com.example.servicemedia.domain.xml.api;

import com.example.servicemedia.domain.xml.enums.DefinitionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XmlDefinitionResponse {
    private String id;
    private String fileName;
    private DefinitionType type;
    private Boolean success;
    private String errorMessage;
    private Long JobExecutionId;
}
