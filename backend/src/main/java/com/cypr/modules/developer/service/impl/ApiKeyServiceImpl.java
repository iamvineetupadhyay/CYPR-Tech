package com.cypr.modules.developer.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.developer.dto.ApiKeyRequestDTO;
import com.cypr.modules.developer.dto.ApiKeyResponseDTO;
import com.cypr.modules.developer.entity.ApiKey;
import com.cypr.modules.developer.mapper.ApiKeyMapper;
import com.cypr.modules.developer.repository.ApiKeyRepository;
import com.cypr.modules.developer.service.ApiKeyService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository repository;
    private final ApiKeyMapper mapper;
    private final UserRepository userRepository;

    public ApiKeyServiceImpl(ApiKeyRepository repository, ApiKeyMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApiKeyResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyResponseDTO getById(UUID id) {
        ApiKey entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ApiKey not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public ApiKeyResponseDTO create(ApiKeyRequestDTO requestDTO) {
        ApiKey entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public ApiKeyResponseDTO update(UUID id, ApiKeyRequestDTO requestDTO) {
        ApiKey entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ApiKey not found with id: " + id));
        entity.setName(requestDTO.getName());
        entity.setKeyHash(requestDTO.getKeyHash());
        entity.setScopes(requestDTO.getScopes());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        entity.setLastUsedAt(requestDTO.getLastUsedAt());
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
        ApiKey entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ApiKey not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
