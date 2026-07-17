package com.cypr.modules.users.service;

import com.cypr.modules.users.dto.PermissionRequestDTO;
import com.cypr.modules.users.dto.PermissionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PermissionService {
    Page<PermissionResponseDTO> getAll(Pageable pageable);
    PermissionResponseDTO getById(UUID id);
    PermissionResponseDTO create(PermissionRequestDTO requestDTO);
    PermissionResponseDTO update(UUID id, PermissionRequestDTO requestDTO);
    void delete(UUID id);
}
