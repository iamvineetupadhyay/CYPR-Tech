package com.cypr.modules.billing.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.billing.dto.SubscriptionRequestDTO;
import com.cypr.modules.billing.dto.SubscriptionResponseDTO;
import com.cypr.modules.billing.entity.Subscription;
import com.cypr.modules.billing.mapper.SubscriptionMapper;
import com.cypr.modules.billing.repository.SubscriptionRepository;
import com.cypr.modules.billing.service.SubscriptionService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;
    private final SubscriptionMapper mapper;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository repository, SubscriptionMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscriptionResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getById(UUID id) {
        Subscription entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Subscription not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SubscriptionResponseDTO create(SubscriptionRequestDTO requestDTO) {
        Subscription entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public SubscriptionResponseDTO update(UUID id, SubscriptionRequestDTO requestDTO) {
        Subscription entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Subscription not found with id: " + id));
        entity.setPlanName(requestDTO.getPlanName());
        entity.setStatus(requestDTO.getStatus());
        entity.setCurrentPeriodStart(requestDTO.getCurrentPeriodStart());
        entity.setCurrentPeriodEnd(requestDTO.getCurrentPeriodEnd());
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
        Subscription entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Subscription not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
