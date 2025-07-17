package com.example.servicemedia.auditlistener.dto;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import com.example.servicemedia.feign.user.UserResponse;
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
