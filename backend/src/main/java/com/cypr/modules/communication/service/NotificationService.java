package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.NotificationRequestDTO;
import com.cypr.modules.communication.dto.NotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface NotificationService {
    Page<NotificationResponseDTO> getAll(Pageable pageable);
    NotificationResponseDTO getById(UUID id);
    NotificationResponseDTO create(NotificationRequestDTO requestDTO);
    NotificationResponseDTO update(UUID id, NotificationRequestDTO requestDTO);
    void delete(UUID id);
}
