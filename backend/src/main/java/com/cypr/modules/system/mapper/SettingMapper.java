package com.cypr.modules.system.mapper;

import com.cypr.modules.system.dto.SettingRequestDTO;
import com.cypr.modules.system.dto.SettingResponseDTO;
import com.cypr.modules.system.entity.Setting;
import org.springframework.stereotype.Component;

@Component
public class SettingMapper {

    public Setting toEntity(SettingRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Setting entity = new Setting();
        entity.setSettingKey(requestDTO.getSettingKey());
        entity.setSettingValue(requestDTO.getSettingValue());
        entity.setGroup(requestDTO.getGroup());
        return entity;
    }

    public SettingResponseDTO toResponseDTO(Setting entity) {
        if (entity == null) return null;
        SettingResponseDTO dto = new SettingResponseDTO();
        dto.setId(entity.getId());
        dto.setSettingKey(entity.getSettingKey());
        dto.setSettingValue(entity.getSettingValue());
        dto.setGroup(entity.getGroup());
        return dto;
    }
}
