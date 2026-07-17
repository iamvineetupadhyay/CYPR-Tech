package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.SessionRequestDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SessionService {
    Page<SessionResponseDTO> getAll(Pageable pageable);
    SessionResponseDTO getById(UUID id);
    SessionResponseDTO create(SessionRequestDTO requestDTO);
    SessionResponseDTO update(UUID id, SessionRequestDTO requestDTO);
    void delete(UUID id);
}
