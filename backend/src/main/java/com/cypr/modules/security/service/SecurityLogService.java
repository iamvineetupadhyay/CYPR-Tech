package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.SecurityLogRequestDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SecurityLogService {
    Page<SecurityLogResponseDTO> getAll(Pageable pageable);
    SecurityLogResponseDTO getById(UUID id);
    SecurityLogResponseDTO create(SecurityLogRequestDTO requestDTO);
    SecurityLogResponseDTO update(UUID id, SecurityLogRequestDTO requestDTO);
    void delete(UUID id);
}
