package com.cypr.modules.users.service;

import com.cypr.modules.users.dto.UserProfileResponseDTO;
import com.cypr.modules.users.dto.UserRequestDTO;
import com.cypr.modules.users.dto.UserResponseDTO;
import com.cypr.modules.users.dto.UserSearchCriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> searchUsers(UserSearchCriteriaDTO criteria, Pageable pageable);
    UserResponseDTO getUserById(Long id);
    UserResponseDTO createUser(UserRequestDTO requestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO);
    void deleteUser(Long id);
    UserProfileResponseDTO getUserProfile(Long id);
    byte[] exportUsersToCsv(UserSearchCriteriaDTO criteria);
}
