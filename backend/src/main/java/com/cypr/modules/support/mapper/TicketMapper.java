package com.cypr.modules.support.mapper;

import com.cypr.modules.support.dto.TicketRequestDTO;
import com.cypr.modules.support.dto.TicketResponseDTO;
import com.cypr.modules.support.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket toEntity(TicketRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Ticket entity = new Ticket();
        entity.setSubject(requestDTO.getSubject());
        entity.setDescription(requestDTO.getDescription());
        entity.setStatus(requestDTO.getStatus());
        entity.setPriority(requestDTO.getPriority());
        return entity;
    }

    public TicketResponseDTO toResponseDTO(Ticket entity) {
        if (entity == null) return null;
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setSubject(entity.getSubject());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        return dto;
    }
}
