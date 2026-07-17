package com.cypr.modules.security.mapper;

import com.cypr.modules.security.dto.SecurityLogRequestDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.entity.SecurityLog;
import org.springframework.stereotype.Component;

@Component
public class SecurityLogMapper {

    public SecurityLog toEntity(SecurityLogRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        SecurityLog entity = new SecurityLog();
        entity.setIpAddress(requestDTO.getIpAddress());
        entity.setSeverity(requestDTO.getSeverity());
        entity.setEvent(requestDTO.getEvent());
        return entity;
    }

    public SecurityLogResponseDTO toResponseDTO(SecurityLog entity) {
        if (entity == null) return null;
        SecurityLogResponseDTO dto = new SecurityLogResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setIpAddress(entity.getIpAddress());
        dto.setSeverity(entity.getSeverity());
        dto.setEvent(entity.getEvent());
        return dto;
    }
}
