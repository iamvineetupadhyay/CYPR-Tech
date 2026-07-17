package com.cypr.modules.security.mapper;

import com.cypr.modules.security.dto.SessionRequestDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;
import com.cypr.modules.security.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public Session toEntity(SessionRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Session entity = new Session();
        entity.setToken(requestDTO.getToken());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        entity.setActive(requestDTO.isActive());
        return entity;
    }

    public SessionResponseDTO toResponseDTO(Session entity) {
        if (entity == null) return null;
        SessionResponseDTO dto = new SessionResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setToken(entity.getToken());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setActive(entity.isActive());
        return dto;
    }
}
