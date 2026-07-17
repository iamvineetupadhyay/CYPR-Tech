package com.cypr.modules.system.service;

import com.cypr.modules.system.dto.SettingRequestDTO;
import com.cypr.modules.system.dto.SettingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SettingService {
    Page<SettingResponseDTO> getAll(Pageable pageable);
    SettingResponseDTO getById(UUID id);
    SettingResponseDTO create(SettingRequestDTO requestDTO);
    SettingResponseDTO update(UUID id, SettingRequestDTO requestDTO);
    void delete(UUID id);
}
