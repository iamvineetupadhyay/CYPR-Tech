package com.cypr.modules.system.service;

import com.cypr.modules.system.dto.FeatureFlagRequestDTO;
import com.cypr.modules.system.dto.FeatureFlagResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface FeatureFlagService {
    Page<FeatureFlagResponseDTO> getAll(Pageable pageable);
    FeatureFlagResponseDTO getById(UUID id);
    FeatureFlagResponseDTO create(FeatureFlagRequestDTO requestDTO);
    FeatureFlagResponseDTO update(UUID id, FeatureFlagRequestDTO requestDTO);
    void delete(UUID id);
}
