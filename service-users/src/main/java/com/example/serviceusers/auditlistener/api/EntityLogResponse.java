package com.example.serviceusers.auditlistener.api;

import com.example.serviceusers.auditlistener.enums.ProcessType;
import com.example.serviceusers.domain.user.api.UserResponse;
import com.example.serviceusers.domain.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityLogResponse {
    private String id;
    private ProcessType process;
    private String entity;
    private String entityId;
    private UserDto user;
}
