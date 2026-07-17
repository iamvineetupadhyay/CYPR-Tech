package com.cypr.modules.communication.mapper;

import com.cypr.modules.communication.dto.NotificationRequestDTO;
import com.cypr.modules.communication.dto.NotificationResponseDTO;
import com.cypr.modules.communication.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Notification entity = new Notification();
        entity.setTitle(requestDTO.getTitle());
        entity.setMessage(requestDTO.getMessage());
        entity.setType(requestDTO.getType());
        entity.setRead(requestDTO.isRead());
        return entity;
    }

    public NotificationResponseDTO toResponseDTO(Notification entity) {
        if (entity == null) return null;
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setType(entity.getType());
        dto.setRead(entity.isRead());
        return dto;
    }
}
