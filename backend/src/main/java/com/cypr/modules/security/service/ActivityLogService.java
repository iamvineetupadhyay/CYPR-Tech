package com.cypr.modules.security.service;

import com.cypr.modules.security.dto.ActivityLogRequestDTO;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ActivityLogService {
    Page<ActivityLogResponseDTO> getAll(Pageable pageable);
    ActivityLogResponseDTO getById(UUID id);
    ActivityLogResponseDTO create(ActivityLogRequestDTO requestDTO);
    ActivityLogResponseDTO update(UUID id, ActivityLogRequestDTO requestDTO);
    void delete(UUID id);
    void logEvent(Long userId, String action, String entityType, String details, String result, Integer scoreDelta, String url, String ipAddress);
}
