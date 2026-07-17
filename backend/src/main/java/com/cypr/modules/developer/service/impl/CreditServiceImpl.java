package com.cypr.modules.developer.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.developer.dto.CreditRequestDTO;
import com.cypr.modules.developer.dto.CreditResponseDTO;
import com.cypr.modules.developer.entity.Credit;
import com.cypr.modules.developer.mapper.CreditMapper;
import com.cypr.modules.developer.repository.CreditRepository;
import com.cypr.modules.developer.service.CreditService;
import com.cypr.entity.User;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CreditServiceImpl implements CreditService {

    private final CreditRepository repository;
    private final CreditMapper mapper;
    private final UserRepository userRepository;
    private final com.cypr.modules.developer.repository.CreditTransactionRepository transactionRepository;
    private final com.cypr.modules.security.repository.AdminAuditLogRepository auditLogRepository;

    public CreditServiceImpl(
            CreditRepository repository,
            CreditMapper mapper,
            UserRepository userRepository,
            com.cypr.modules.developer.repository.CreditTransactionRepository transactionRepository,
            com.cypr.modules.security.repository.AdminAuditLogRepository auditLogRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CreditResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditResponseDTO getById(UUID id) {
        Credit entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Credit not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public CreditResponseDTO create(CreditRequestDTO requestDTO) {
        Credit entity = mapper.toEntity(requestDTO);
        if (requestDTO.getUserId() != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public CreditResponseDTO update(UUID id, CreditRequestDTO requestDTO) {
        Credit entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Credit not found with id: " + id));
        entity.setBalance(requestDTO.getBalance());
        entity.setTotalPurchased(requestDTO.getTotalPurchased());
        entity.setTotalConsumed(requestDTO.getTotalConsumed());
        if (requestDTO.getUserId() != null && (entity.getUser() == null || !entity.getUser().getId().equals(requestDTO.getUserId()))) {
            User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
            entity.setUser(user);
        }
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional
    public CreditResponseDTO adjustCredits(Long targetUserId, Long adminId, Integer amountDelta, String operation, String reason, String ipAddress, String correlationId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + targetUserId));

        Credit credit = repository.findAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseGet(() -> {
                Credit newCredit = new Credit();
                newCredit.setUser(user);
                newCredit.setBalance((long) user.getCredits());
                    newCredit.setTotalPurchased(0L);
                    newCredit.setTotalConsumed(0L);
                    return newCredit;
                });

        long currentBalance = credit.getBalance() != null ? credit.getBalance() : 0L;
        long newBalance = currentBalance + amountDelta;
        if (newBalance < 0) newBalance = 0;
        credit.setBalance(newBalance);
        user.setCredits(Math.toIntExact(newBalance));

        if (amountDelta > 0) {
            credit.setTotalPurchased((credit.getTotalPurchased() != null ? credit.getTotalPurchased() : 0L) + amountDelta);
        }

        credit = repository.save(credit);

        // Save immutable CreditTransaction row
        transactionRepository.save(new com.cypr.modules.developer.entity.CreditTransaction(
                targetUserId, adminId, amountDelta, operation, reason, ipAddress, correlationId));

        // Save immutable AdminAuditLog row
        auditLogRepository.save(new com.cypr.modules.security.entity.AdminAuditLog(
                adminId, "admin", "CREDIT_ADJUST",
                "Adjusted credits for User #" + targetUserId + " by " + amountDelta + " pts. Reason: " + reason,
                ipAddress, correlationId));

        return mapper.toResponseDTO(credit);
    }

    @Override
    public void delete(UUID id) {
        Credit entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Credit not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
