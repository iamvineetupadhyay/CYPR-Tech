package com.cypr.modules.users.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.users.dto.PermissionRequestDTO;
import com.cypr.modules.users.dto.PermissionResponseDTO;
import com.cypr.modules.users.entity.Permission;
import com.cypr.modules.users.mapper.PermissionMapper;
import com.cypr.modules.users.repository.PermissionRepository;
import com.cypr.modules.users.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository repository;
    private final PermissionMapper mapper;

    public PermissionServiceImpl(PermissionRepository repository, PermissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDTO getById(UUID id) {
        Permission entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Permission not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public PermissionResponseDTO create(PermissionRequestDTO requestDTO) {
        Permission entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public PermissionResponseDTO update(UUID id, PermissionRequestDTO requestDTO) {
        Permission entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Permission not found with id: " + id));
        entity.setName(requestDTO.getName());
        entity.setModule(requestDTO.getModule());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        Permission entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Permission not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
