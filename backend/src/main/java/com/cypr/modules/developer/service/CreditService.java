package com.cypr.modules.developer.service;

import com.cypr.modules.developer.dto.CreditRequestDTO;
import com.cypr.modules.developer.dto.CreditResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CreditService {
    Page<CreditResponseDTO> getAll(Pageable pageable);
    CreditResponseDTO getById(UUID id);
    CreditResponseDTO create(CreditRequestDTO requestDTO);
    CreditResponseDTO update(UUID id, CreditRequestDTO requestDTO);
    void delete(UUID id);
    CreditResponseDTO adjustCredits(Long targetUserId, Long adminId, Integer amountDelta, String operation, String reason, String ipAddress, String correlationId);
}
