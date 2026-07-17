package com.cypr.modules.system.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.system.dto.FeatureFlagRequestDTO;
import com.cypr.modules.system.dto.FeatureFlagResponseDTO;
import com.cypr.modules.system.entity.FeatureFlag;
import com.cypr.modules.system.mapper.FeatureFlagMapper;
import com.cypr.modules.system.repository.FeatureFlagRepository;
import com.cypr.modules.system.service.FeatureFlagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final FeatureFlagRepository repository;
    private final FeatureFlagMapper mapper;

    public FeatureFlagServiceImpl(FeatureFlagRepository repository, FeatureFlagMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeatureFlagResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public FeatureFlagResponseDTO getById(UUID id) {
        FeatureFlag entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("FeatureFlag not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public FeatureFlagResponseDTO create(FeatureFlagRequestDTO requestDTO) {
        FeatureFlag entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public FeatureFlagResponseDTO update(UUID id, FeatureFlagRequestDTO requestDTO) {
        FeatureFlag entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("FeatureFlag not found with id: " + id));
        entity.setFlagKey(requestDTO.getFlagKey());
        entity.setEnabled(requestDTO.isEnabled());
        entity.setDescription(requestDTO.getDescription());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        FeatureFlag entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("FeatureFlag not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
