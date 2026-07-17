package com.cypr.modules.communication.mapper;

import com.cypr.modules.communication.dto.AnnouncementRequestDTO;
import com.cypr.modules.communication.dto.AnnouncementResponseDTO;
import com.cypr.modules.communication.entity.Announcement;
import org.springframework.stereotype.Component;

@Component
public class AnnouncementMapper {

    public Announcement toEntity(AnnouncementRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Announcement entity = new Announcement();
        entity.setTitle(requestDTO.getTitle());
        entity.setContent(requestDTO.getContent());
        entity.setStartsAt(requestDTO.getStartsAt());
        entity.setEndsAt(requestDTO.getEndsAt());
        entity.setActive(requestDTO.isActive());
        return entity;
    }

    public AnnouncementResponseDTO toResponseDTO(Announcement entity) {
        if (entity == null) return null;
        AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setStartsAt(entity.getStartsAt());
        dto.setEndsAt(entity.getEndsAt());
        dto.setActive(entity.isActive());
        return dto;
    }
}
