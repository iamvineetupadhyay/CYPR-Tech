package com.cypr.modules.security.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.security.dto.SessionRequestDTO;
import com.cypr.modules.security.dto.SessionResponseDTO;
import com.cypr.modules.security.entity.Session;
import com.cypr.modules.security.mapper.SessionMapper;
import com.cypr.modules.security.repository.SessionRepository;
import com.cypr.modules.security.service.SessionService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;
    private final SessionMapper mapper;
    private final UserRepository userRepository;

    public SessionServiceImpl(SessionRepository repository, SessionMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SessionResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDTO getById(UUID id) {
        Session entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Session not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SessionResponseDTO create(SessionRequestDTO requestDTO) {
        Session entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SessionResponseDTO update(UUID id, SessionRequestDTO requestDTO) {
        Session entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Session not found with id: " + id));
        entity.setToken(requestDTO.getToken());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        entity.setActive(requestDTO.isActive());
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
        Session entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Session not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
