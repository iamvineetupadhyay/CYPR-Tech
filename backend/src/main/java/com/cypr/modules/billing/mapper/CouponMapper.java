package com.cypr.modules.billing.mapper;

import com.cypr.modules.billing.dto.CouponRequestDTO;
import com.cypr.modules.billing.dto.CouponResponseDTO;
import com.cypr.modules.billing.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public Coupon toEntity(CouponRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Coupon entity = new Coupon();
        entity.setCode(requestDTO.getCode());
        entity.setDiscountPercentage(requestDTO.getDiscountPercentage());
        entity.setMaxUses(requestDTO.getMaxUses());
        entity.setUsesCount(requestDTO.getUsesCount());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        return entity;
    }

    public CouponResponseDTO toResponseDTO(Coupon entity) {
        if (entity == null) return null;
        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setMaxUses(entity.getMaxUses());
        dto.setUsesCount(entity.getUsesCount());
        dto.setExpiresAt(entity.getExpiresAt());
        return dto;
    }
}
