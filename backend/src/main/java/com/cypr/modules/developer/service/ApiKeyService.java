package com.cypr.modules.developer.service;

import com.cypr.modules.developer.dto.ApiKeyRequestDTO;
import com.cypr.modules.developer.dto.ApiKeyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ApiKeyService {
    Page<ApiKeyResponseDTO> getAll(Pageable pageable);
    ApiKeyResponseDTO getById(UUID id);
    ApiKeyResponseDTO create(ApiKeyRequestDTO requestDTO);
    ApiKeyResponseDTO update(UUID id, ApiKeyRequestDTO requestDTO);
    void delete(UUID id);
}
