package com.cypr.modules.system.service;

import com.cypr.modules.system.dto.BackupHistoryRequestDTO;
import com.cypr.modules.system.dto.BackupHistoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface BackupHistoryService {
    Page<BackupHistoryResponseDTO> getAll(Pageable pageable);
    BackupHistoryResponseDTO getById(UUID id);
    BackupHistoryResponseDTO create(BackupHistoryRequestDTO requestDTO);
    BackupHistoryResponseDTO update(UUID id, BackupHistoryRequestDTO requestDTO);
    void delete(UUID id);
}
