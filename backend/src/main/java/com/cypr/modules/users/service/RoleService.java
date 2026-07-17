package com.cypr.modules.users.service;

import com.cypr.modules.users.dto.RoleRequestDTO;
import com.cypr.modules.users.dto.RoleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface RoleService {
    Page<RoleResponseDTO> getAll(Pageable pageable);
    RoleResponseDTO getById(UUID id);
    RoleResponseDTO create(RoleRequestDTO requestDTO);
    RoleResponseDTO update(UUID id, RoleRequestDTO requestDTO);
    void delete(UUID id);
}
