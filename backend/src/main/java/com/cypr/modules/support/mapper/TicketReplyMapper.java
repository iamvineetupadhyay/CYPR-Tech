package com.cypr.modules.support.mapper;

import com.cypr.modules.support.dto.TicketReplyRequestDTO;
import com.cypr.modules.support.dto.TicketReplyResponseDTO;
import com.cypr.modules.support.entity.TicketReply;
import org.springframework.stereotype.Component;

@Component
public class TicketReplyMapper {

    public TicketReply toEntity(TicketReplyRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        TicketReply entity = new TicketReply();
        entity.setMessage(requestDTO.getMessage());
        return entity;
    }

    public TicketReplyResponseDTO toResponseDTO(TicketReply entity) {
        if (entity == null) return null;
        TicketReplyResponseDTO dto = new TicketReplyResponseDTO();
        dto.setId(entity.getId());
        if (entity.getTicket() != null) dto.setTicketId(entity.getTicket().getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setMessage(entity.getMessage());
        return dto;
    }
}
