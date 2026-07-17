package com.cypr.modules.security.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.ActivityLogRequestDTO;
import com.cypr.modules.security.dto.ActivityLogResponseDTO;
import com.cypr.modules.security.entity.ActivityLog;
import com.cypr.modules.security.mapper.ActivityLogMapper;
import com.cypr.modules.security.repository.ActivityLogRepository;
import com.cypr.modules.security.service.ActivityLogService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository repository;
    private final ActivityLogMapper mapper;
    private final UserRepository userRepository;

    public ActivityLogServiceImpl(ActivityLogRepository repository, ActivityLogMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLogResponseDTO getById(UUID id) {
        ActivityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ActivityLog not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public ActivityLogResponseDTO create(ActivityLogRequestDTO requestDTO) {
        ActivityLog entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public ActivityLogResponseDTO update(UUID id, ActivityLogRequestDTO requestDTO) {
        ActivityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ActivityLog not found with id: " + id));
        entity.setAction(requestDTO.getAction());
        entity.setEntityType(requestDTO.getEntityType());
        entity.setEntityId(requestDTO.getEntityId());
        entity.setDetails(requestDTO.getDetails());
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
        ActivityLog entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("ActivityLog not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Override
    public void logEvent(Long userId, String action, String entityType, String details, String result, Integer scoreDelta, String url, String ipAddress) {
        ActivityLog log = new ActivityLog();
        if (userId != null) {
            userRepository.findById(userId).ifPresent(log::setUser);
        }
        log.setAction(action);
        log.setEntityType(entityType);
        log.setDetails(details);
        log.setResult(result);
        log.setScoreDelta(scoreDelta);
        log.setUrl(url);
        log.setIpAddress(ipAddress);
        repository.save(log);
    }
}
