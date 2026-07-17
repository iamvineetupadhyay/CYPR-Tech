package com.cypr.modules.communication.mapper;

import com.cypr.modules.communication.dto.HomepageBannerRequestDTO;
import com.cypr.modules.communication.dto.HomepageBannerResponseDTO;
import com.cypr.modules.communication.entity.HomepageBanner;
import org.springframework.stereotype.Component;

@Component
public class HomepageBannerMapper {

    public HomepageBanner toEntity(HomepageBannerRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        HomepageBanner entity = new HomepageBanner();
        entity.setImageUrl(requestDTO.getImageUrl());
        entity.setLinkUrl(requestDTO.getLinkUrl());
        entity.setActive(requestDTO.isActive());
        return entity;
    }

    public HomepageBannerResponseDTO toResponseDTO(HomepageBanner entity) {
        if (entity == null) return null;
        HomepageBannerResponseDTO dto = new HomepageBannerResponseDTO();
        dto.setId(entity.getId());
        dto.setImageUrl(entity.getImageUrl());
        dto.setLinkUrl(entity.getLinkUrl());
        dto.setActive(entity.isActive());
        return dto;
    }
}
