package com.cypr.modules.billing.mapper;

import com.cypr.modules.billing.dto.PaymentRequestDTO;
import com.cypr.modules.billing.dto.PaymentResponseDTO;
import com.cypr.modules.billing.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Payment entity = new Payment();
        entity.setAmount(requestDTO.getAmount());
        entity.setCurrency(requestDTO.getCurrency());
        entity.setProvider(requestDTO.getProvider());
        entity.setTransactionId(requestDTO.getTransactionId());
        entity.setStatus(requestDTO.getStatus());
        return entity;
    }

    public PaymentResponseDTO toResponseDTO(Payment entity) {
        if (entity == null) return null;
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setProvider(entity.getProvider());
        dto.setTransactionId(entity.getTransactionId());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
