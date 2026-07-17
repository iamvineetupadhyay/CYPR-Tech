package com.cypr.modules.security.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.SecurityLogRequestDTO;
import com.cypr.modules.security.dto.SecurityLogResponseDTO;
import com.cypr.modules.security.entity.SecurityLog;
import com.cypr.modules.security.mapper.SecurityLogMapper;
import com.cypr.modules.security.repository.SecurityLogRepository;
import com.cypr.modules.security.service.SecurityLogService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class SecurityLogServiceImpl implements SecurityLogService {

    private final SecurityLogRepository repository;
    private final SecurityLogMapper mapper;
    private final UserRepository userRepository;

    public SecurityLogServiceImpl(SecurityLogRepository repository, SecurityLogMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SecurityLogResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityLogResponseDTO getById(UUID id) {
        SecurityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("SecurityLog not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SecurityLogResponseDTO create(SecurityLogRequestDTO requestDTO) {
        SecurityLog entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SecurityLogResponseDTO update(UUID id, SecurityLogRequestDTO requestDTO) {
        SecurityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("SecurityLog not found with id: " + id));
        entity.setIpAddress(requestDTO.getIpAddress());
        entity.setSeverity(requestDTO.getSeverity());
        entity.setEvent(requestDTO.getEvent());
        if (requestDTO.getUserId() != null && (entity.getUser() == null || !entity.getUser().getId().equals(requestDTO.getUserId()))) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        SecurityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("SecurityLog not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
