package com.cypr.modules.system.mapper;

import com.cypr.modules.system.dto.BackupHistoryRequestDTO;
import com.cypr.modules.system.dto.BackupHistoryResponseDTO;
import com.cypr.modules.system.entity.BackupHistory;
import org.springframework.stereotype.Component;

@Component
public class BackupHistoryMapper {

    public BackupHistory toEntity(BackupHistoryRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        BackupHistory entity = new BackupHistory();
        entity.setFileName(requestDTO.getFileName());
        entity.setSizeBytes(requestDTO.getSizeBytes());
        entity.setStatus(requestDTO.getStatus());
        entity.setCompletedAt(requestDTO.getCompletedAt());
        return entity;
    }

    public BackupHistoryResponseDTO toResponseDTO(BackupHistory entity) {
        if (entity == null) return null;
        BackupHistoryResponseDTO dto = new BackupHistoryResponseDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setSizeBytes(entity.getSizeBytes());
        dto.setStatus(entity.getStatus());
        dto.setCompletedAt(entity.getCompletedAt());
        return dto;
    }
}
