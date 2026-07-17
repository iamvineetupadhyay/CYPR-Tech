package com.cypr.modules.security.mapper;

import com.cypr.modules.security.dto.ActivityLogRequestDTO;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import com.cypr.modules.security.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLog toEntity(ActivityLogRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        ActivityLog entity = new ActivityLog();
        entity.setAction(requestDTO.getAction());
        entity.setEntityType(requestDTO.getEntityType());
        entity.setEntityId(requestDTO.getEntityId());
        entity.setDetails(requestDTO.getDetails());
        return entity;
    }

    public ActivityLogResponseDTO toResponseDTO(ActivityLog entity) {
        if (entity == null) return null;
        ActivityLogResponseDTO dto = new ActivityLogResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setAction(entity.getAction());
        dto.setEntityType(entity.getEntityType());
        dto.setEntityId(entity.getEntityId());
        dto.setDetails(entity.getDetails());
        dto.setScoreDelta(entity.getScoreDelta());
        dto.setResult(entity.getResult());
        dto.setUrl(entity.getUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
