package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.DeviceIPRequestDTO;
import com.cypr.modules.security.dto.DeviceIPResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface DeviceIPService {
    Page<DeviceIPResponseDTO> getAll(Pageable pageable);
    DeviceIPResponseDTO getById(UUID id);
    DeviceIPResponseDTO create(DeviceIPRequestDTO requestDTO);
    DeviceIPResponseDTO update(UUID id, DeviceIPRequestDTO requestDTO);
    void delete(UUID id);
}
