package com.cypr.modules.users.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.users.dto.RoleRequestDTO;
import com.cypr.modules.users.dto.RoleResponseDTO;
import com.cypr.modules.users.entity.Role;
import com.cypr.modules.users.mapper.RoleMapper;
import com.cypr.modules.users.repository.RoleRepository;
import com.cypr.modules.users.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    public RoleServiceImpl(RoleRepository repository, RoleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO getById(UUID id) {
        Role entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Role not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public RoleResponseDTO create(RoleRequestDTO requestDTO) {
        Role entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public RoleResponseDTO update(UUID id, RoleRequestDTO requestDTO) {
        Role entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Role not found with id: " + id));
        entity.setName(requestDTO.getName());
        entity.setDescription(requestDTO.getDescription());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        Role entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Role not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
