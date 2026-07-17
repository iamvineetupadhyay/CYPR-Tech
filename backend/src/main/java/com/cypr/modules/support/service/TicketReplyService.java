package com.cypr.modules.support.service;

import com.cypr.modules.support.dto.TicketReplyRequestDTO;
import com.cypr.modules.support.dto.TicketReplyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TicketReplyService {
    Page<TicketReplyResponseDTO> getAll(Pageable pageable);
    TicketReplyResponseDTO getById(UUID id);
    TicketReplyResponseDTO create(TicketReplyRequestDTO requestDTO);
    TicketReplyResponseDTO update(UUID id, TicketReplyRequestDTO requestDTO);
    void delete(UUID id);
}
