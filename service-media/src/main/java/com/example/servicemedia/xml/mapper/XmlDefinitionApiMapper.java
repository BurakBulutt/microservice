package com.example.servicemedia.xml.mapper;

import com.example.servicemedia.xml.api.XmlDefinitionResponse;
import com.example.servicemedia.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.xml.model.XmlDefinition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XmlDefinitionApiMapper {

    public static XmlDefinitionResponse toResponse(XmlDefinitionDto xmlDefinitionDto) {
        return XmlDefinitionResponse.builder()
                .fileName(xmlDefinitionDto.getFileName())
                .success(xmlDefinitionDto.getSuccess())
                .type(xmlDefinitionDto.getType())
                .errorMessage(xmlDefinitionDto.getErrorMessage())
                .build();
    }

    public static Page<XmlDefinitionResponse> toPageResponse(Page<XmlDefinitionDto> page) {
        return page.map(XmlDefinitionApiMapper::toResponse);
    }
}
