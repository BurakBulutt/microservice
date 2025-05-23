package com.example.servicemedia.domain.xml.mapper;

import com.example.servicemedia.domain.xml.api.XmlDefinitionResponse;
import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlDefinitionApiMapper {

    public static XmlDefinitionResponse toResponse(XmlDefinitionDto xmlDefinitionDto) {
        return XmlDefinitionResponse.builder()
                .id(xmlDefinitionDto.getId())
                .fileName(xmlDefinitionDto.getFileName())
                .success(xmlDefinitionDto.getSuccess())
                .type(xmlDefinitionDto.getType())
                .errorMessage(xmlDefinitionDto.getErrorMessage())
                .JobExecutionId(xmlDefinitionDto.getJobExecutionId())
                .build();
    }

    public static Page<XmlDefinitionResponse> toPageResponse(Page<XmlDefinitionDto> page) {
        return page.map(XmlDefinitionApiMapper::toResponse);
    }
}
