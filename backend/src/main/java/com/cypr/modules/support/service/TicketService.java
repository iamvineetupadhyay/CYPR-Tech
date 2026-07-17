package com.cypr.modules.support.service;

import com.cypr.modules.support.dto.TicketRequestDTO;
import com.cypr.modules.support.dto.TicketResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TicketService {
    Page<TicketResponseDTO> getAll(Pageable pageable);
    TicketResponseDTO getById(UUID id);
    TicketResponseDTO create(TicketRequestDTO requestDTO);
    TicketResponseDTO update(UUID id, TicketRequestDTO requestDTO);
    void delete(UUID id);
}
