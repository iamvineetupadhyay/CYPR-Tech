package com.cypr.modules.system.mapper;

import com.cypr.modules.system.dto.FeatureFlagRequestDTO;
import com.cypr.modules.system.dto.FeatureFlagResponseDTO;
import com.cypr.modules.system.entity.FeatureFlag;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagMapper {

    public FeatureFlag toEntity(FeatureFlagRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        FeatureFlag entity = new FeatureFlag();
        entity.setFlagKey(requestDTO.getFlagKey());
        entity.setEnabled(requestDTO.isEnabled());
        entity.setDescription(requestDTO.getDescription());
        return entity;
    }

    public FeatureFlagResponseDTO toResponseDTO(FeatureFlag entity) {
        if (entity == null) return null;
        FeatureFlagResponseDTO dto = new FeatureFlagResponseDTO();
        dto.setId(entity.getId());
        dto.setFlagKey(entity.getFlagKey());
        dto.setEnabled(entity.isEnabled());
        dto.setDescription(entity.getDescription());
        return dto;
    }
}
