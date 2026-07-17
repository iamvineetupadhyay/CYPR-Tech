package com.cypr.modules.security.mapper;

import com.cypr.modules.security.dto.DeviceIPRequestDTO;
import com.cypr.modules.security.dto.DeviceIPResponseDTO;
import com.cypr.modules.security.entity.DeviceIP;
import org.springframework.stereotype.Component;

@Component
public class DeviceIPMapper {

    public DeviceIP toEntity(DeviceIPRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        DeviceIP entity = new DeviceIP();
        entity.setIpAddress(requestDTO.getIpAddress());
        entity.setLastSeenAt(requestDTO.getLastSeenAt());
        return entity;
    }

    public DeviceIPResponseDTO toResponseDTO(DeviceIP entity) {
        if (entity == null) return null;
        DeviceIPResponseDTO dto = new DeviceIPResponseDTO();
        dto.setId(entity.getId());
        if (entity.getDevice() != null) dto.setDeviceId(entity.getDevice().getId());
        dto.setIpAddress(entity.getIpAddress());
        dto.setLastSeenAt(entity.getLastSeenAt());
        return dto;
    }
}
