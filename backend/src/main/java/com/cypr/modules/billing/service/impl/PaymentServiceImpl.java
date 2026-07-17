package com.cypr.modules.billing.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.billing.dto.PaymentRequestDTO;
import com.cypr.modules.billing.dto.PaymentResponseDTO;
import com.cypr.modules.billing.entity.Payment;
import com.cypr.modules.billing.mapper.PaymentMapper;
import com.cypr.modules.billing.repository.PaymentRepository;
import com.cypr.modules.billing.service.PaymentService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final UserRepository userRepository;

    public PaymentServiceImpl(PaymentRepository repository, PaymentMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getById(UUID id) {
        Payment entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Payment not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public PaymentResponseDTO create(PaymentRequestDTO requestDTO) {
        Payment entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public PaymentResponseDTO update(UUID id, PaymentRequestDTO requestDTO) {
        Payment entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Payment not found with id: " + id));
        entity.setAmount(requestDTO.getAmount());
        entity.setCurrency(requestDTO.getCurrency());
        entity.setProvider(requestDTO.getProvider());
        entity.setTransactionId(requestDTO.getTransactionId());
        entity.setStatus(requestDTO.getStatus());
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
        Payment entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Payment not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
