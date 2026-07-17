package com.cypr.modules.billing.mapper;

import com.cypr.modules.billing.dto.SubscriptionRequestDTO;
import com.cypr.modules.billing.dto.SubscriptionResponseDTO;
import com.cypr.modules.billing.entity.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public Subscription toEntity(SubscriptionRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Subscription entity = new Subscription();
        entity.setPlanName(requestDTO.getPlanName());
        entity.setStatus(requestDTO.getStatus());
        entity.setCurrentPeriodStart(requestDTO.getCurrentPeriodStart());
        entity.setCurrentPeriodEnd(requestDTO.getCurrentPeriodEnd());
        return entity;
    }

    public SubscriptionResponseDTO toResponseDTO(Subscription entity) {
        if (entity == null) return null;
        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setPlanName(entity.getPlanName());
        dto.setStatus(entity.getStatus());
        dto.setCurrentPeriodStart(entity.getCurrentPeriodStart());
        dto.setCurrentPeriodEnd(entity.getCurrentPeriodEnd());
        return dto;
    }
}
