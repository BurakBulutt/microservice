package com.example.serviceusers.auditlistener.dto;

import com.example.serviceusers.auditlistener.enums.ProcessType;
import com.example.serviceusers.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityLogDto {
    private String id;
    private ProcessType process;
    private String entity;
    private String entityId;
    private UserDto user;
}
