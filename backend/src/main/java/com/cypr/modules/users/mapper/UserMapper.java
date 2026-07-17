package com.cypr.modules.users.mapper;

import com.cypr.entity.User;
import com.cypr.modules.users.dto.RoleResponseDTO;
import com.cypr.modules.users.dto.UserRequestDTO;
import com.cypr.modules.users.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setMobile(user.getMobile());
        dto.setEnabled(user.isEnabled());
        dto.setSafetyScore(user.getSafetyScore());
        dto.setCredits(user.getCredits());
        dto.setSubscriptionType(user.getSubscriptionType());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream().map(role -> {
                RoleResponseDTO roleDto = new RoleResponseDTO();
                roleDto.setId(role.getId());
                roleDto.setName(role.getName());
                roleDto.setDescription(role.getDescription());
                return roleDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    public void updateEntityFromRequestDTO(UserRequestDTO requestDTO, User user) {
        if (requestDTO == null || user == null) {
            return;
        }
        
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setUsername(requestDTO.getUsername());
        user.setMobile(requestDTO.getMobile());
        user.setBio(requestDTO.getBio());
        user.setEnabled(requestDTO.isEnabled());
        
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            user.setPassword(requestDTO.getPassword());
        }

        if (requestDTO.getCredits() > 0) {
            user.setCredits(requestDTO.getCredits());
        }

        if (requestDTO.getSubscriptionType() != null) {
            user.setSubscriptionType(requestDTO.getSubscriptionType());
        }
    }
}
