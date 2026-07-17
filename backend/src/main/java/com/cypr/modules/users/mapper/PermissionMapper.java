package com.cypr.modules.users.mapper;

import com.cypr.modules.users.dto.PermissionRequestDTO;
import com.cypr.modules.users.dto.PermissionResponseDTO;
import com.cypr.modules.users.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public Permission toEntity(PermissionRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Permission permission = new Permission();
        permission.setName(requestDTO.getName());
        permission.setModule(requestDTO.getModule());
        return permission;
    }

    public PermissionResponseDTO toResponseDTO(Permission permission) {
        if (permission == null) return null;
        PermissionResponseDTO dto = new PermissionResponseDTO();
        dto.setId(permission.getId());
        dto.setName(permission.getName());
        dto.setModule(permission.getModule());
        return dto;
    }
}
