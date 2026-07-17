package com.cypr.modules.system.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.system.dto.SettingRequestDTO;
import com.cypr.modules.system.dto.SettingResponseDTO;
import com.cypr.modules.system.entity.Setting;
import com.cypr.modules.system.mapper.SettingMapper;
import com.cypr.modules.system.repository.SettingRepository;
import com.cypr.modules.system.service.SettingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {

    private final SettingRepository repository;
    private final SettingMapper mapper;

    public SettingServiceImpl(SettingRepository repository, SettingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "settings")
    public Page<SettingResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = "settings", key = "#id")
    public SettingResponseDTO getById(UUID id) {
        Setting entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Setting not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "settings", allEntries = true)
    public SettingResponseDTO create(SettingRequestDTO requestDTO) {
        Setting entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "settings", allEntries = true)
    public SettingResponseDTO update(UUID id, SettingRequestDTO requestDTO) {
        Setting entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Setting not found with id: " + id));
        entity.setSettingKey(requestDTO.getSettingKey());
        entity.setSettingValue(requestDTO.getSettingValue());
        entity.setGroup(requestDTO.getGroup());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @org.springframework.cache.annotation.CacheEvict(value = "settings", allEntries = true)
    public void delete(UUID id) {
        Setting entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Setting not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
