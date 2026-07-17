package com.cypr.modules.developer.mapper;

import com.cypr.modules.developer.dto.ApiKeyRequestDTO;
import com.cypr.modules.developer.dto.ApiKeyResponseDTO;
import com.cypr.modules.developer.entity.ApiKey;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyMapper {

    public ApiKey toEntity(ApiKeyRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        ApiKey entity = new ApiKey();
        entity.setName(requestDTO.getName());
        entity.setKeyHash(requestDTO.getKeyHash());
        entity.setScopes(requestDTO.getScopes());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        entity.setLastUsedAt(requestDTO.getLastUsedAt());
        return entity;
    }

    public ApiKeyResponseDTO toResponseDTO(ApiKey entity) {
        if (entity == null) return null;
        ApiKeyResponseDTO dto = new ApiKeyResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setName(entity.getName());
        dto.setKeyHash(entity.getKeyHash());
        dto.setScopes(entity.getScopes());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setLastUsedAt(entity.getLastUsedAt());
        return dto;
    }
}
