package com.cypr.modules.users.mapper;

import com.cypr.modules.users.dto.RoleRequestDTO;
import com.cypr.modules.users.dto.RoleResponseDTO;
import com.cypr.modules.users.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toEntity(RoleRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Role role = new Role();
        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());
        return role;
    }

    public RoleResponseDTO toResponseDTO(Role role) {
        if (role == null) return null;
        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        return dto;
    }
}
