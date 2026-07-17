package com.cypr.modules.system.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.system.dto.BackupHistoryRequestDTO;
import com.cypr.modules.system.dto.BackupHistoryResponseDTO;
import com.cypr.modules.system.entity.BackupHistory;
import com.cypr.modules.system.mapper.BackupHistoryMapper;
import com.cypr.modules.system.repository.BackupHistoryRepository;
import com.cypr.modules.system.service.BackupHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BackupHistoryServiceImpl implements BackupHistoryService {

    private final BackupHistoryRepository repository;
    private final BackupHistoryMapper mapper;

    public BackupHistoryServiceImpl(BackupHistoryRepository repository, BackupHistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BackupHistoryResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BackupHistoryResponseDTO getById(UUID id) {
        BackupHistory entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("BackupHistory not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public BackupHistoryResponseDTO create(BackupHistoryRequestDTO requestDTO) {
        BackupHistory entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public BackupHistoryResponseDTO update(UUID id, BackupHistoryRequestDTO requestDTO) {
        BackupHistory entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("BackupHistory not found with id: " + id));
        entity.setFileName(requestDTO.getFileName());
        entity.setSizeBytes(requestDTO.getSizeBytes());
        entity.setStatus(requestDTO.getStatus());
        entity.setCompletedAt(requestDTO.getCompletedAt());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        BackupHistory entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("BackupHistory not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
