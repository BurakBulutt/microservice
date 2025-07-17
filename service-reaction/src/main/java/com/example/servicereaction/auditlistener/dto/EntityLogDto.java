package com.example.servicereaction.auditlistener.dto;

import com.example.servicereaction.auditlistener.enums.ProcessType;
import com.example.servicereaction.feign.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityLogDto {
    private String id;
    private ProcessType process;
    private String entity;
    private String entityId;
    private UserResponse user;
}
