package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.DeviceRequestDTO;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface DeviceService {
    Page<DeviceResponseDTO> getAll(Pageable pageable);
    DeviceResponseDTO getById(UUID id);
    DeviceResponseDTO create(DeviceRequestDTO requestDTO);
    DeviceResponseDTO update(UUID id, DeviceRequestDTO requestDTO);
    void delete(UUID id);
}
