package com.cypr.modules.communication.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.NotificationRequestDTO;
import com.cypr.modules.communication.dto.NotificationResponseDTO;
import com.cypr.modules.communication.entity.Notification;
import com.cypr.modules.communication.mapper.NotificationMapper;
import com.cypr.modules.communication.repository.NotificationRepository;
import com.cypr.modules.communication.service.NotificationService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository repository, NotificationMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDTO getById(UUID id) {
        Notification entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Notification not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public NotificationResponseDTO create(NotificationRequestDTO requestDTO) {
        Notification entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public NotificationResponseDTO update(UUID id, NotificationRequestDTO requestDTO) {
        Notification entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Notification not found with id: " + id));
        entity.setTitle(requestDTO.getTitle());
        entity.setMessage(requestDTO.getMessage());
        entity.setType(requestDTO.getType());
        entity.setRead(requestDTO.isRead());
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
        Notification entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Notification not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
