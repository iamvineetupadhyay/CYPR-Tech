package com.cypr.modules.security.mapper;

import com.cypr.modules.security.dto.DeviceRequestDTO;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import com.cypr.modules.security.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    public Device toEntity(DeviceRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Device entity = new Device();
        entity.setFingerprint(requestDTO.getFingerprint());
        entity.setUserAgent(requestDTO.getUserAgent());
        entity.setOs(requestDTO.getOs());
        entity.setBrowser(requestDTO.getBrowser());
        return entity;
    }

    public DeviceResponseDTO toResponseDTO(Device entity) {
        if (entity == null) return null;
        DeviceResponseDTO dto = new DeviceResponseDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        dto.setFingerprint(entity.getFingerprint());
        dto.setUserAgent(entity.getUserAgent());
        dto.setOs(entity.getOs());
        dto.setBrowser(entity.getBrowser());
        return dto;
    }
}
