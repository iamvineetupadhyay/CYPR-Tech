package com.cypr.modules.developer.mapper;

import com.cypr.modules.developer.dto.CreditRequestDTO;
import com.cypr.modules.developer.dto.CreditResponseDTO;
import com.cypr.modules.developer.entity.Credit;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper {

    public Credit toEntity(CreditRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Credit entity = new Credit();
        entity.setBalance(requestDTO.getBalance());
        entity.setTotalPurchased(requestDTO.getTotalPurchased());
        entity.setTotalConsumed(requestDTO.getTotalConsumed());
        return entity;
    }

    public CreditResponseDTO toResponseDTO(Credit entity) {
        if (entity == null) return null;
        CreditResponseDTO dto = new CreditResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setBalance(entity.getBalance());
        dto.setTotalPurchased(entity.getTotalPurchased());
        dto.setTotalConsumed(entity.getTotalConsumed());
        return dto;
    }
}
