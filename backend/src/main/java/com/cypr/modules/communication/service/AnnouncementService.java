package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.AnnouncementRequestDTO;
import com.cypr.modules.communication.dto.AnnouncementResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface AnnouncementService {
    Page<AnnouncementResponseDTO> getAll(Pageable pageable);
    AnnouncementResponseDTO getById(UUID id);
    AnnouncementResponseDTO create(AnnouncementRequestDTO requestDTO);
    AnnouncementResponseDTO update(UUID id, AnnouncementRequestDTO requestDTO);
    void delete(UUID id);
}
