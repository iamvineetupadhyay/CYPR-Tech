package com.cypr.modules.billing.service;

import com.cypr.modules.billing.dto.SubscriptionRequestDTO;
import com.cypr.modules.billing.dto.SubscriptionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SubscriptionService {
    Page<SubscriptionResponseDTO> getAll(Pageable pageable);
    SubscriptionResponseDTO getById(UUID id);
    SubscriptionResponseDTO create(SubscriptionRequestDTO requestDTO);
    SubscriptionResponseDTO update(UUID id, SubscriptionRequestDTO requestDTO);
    void delete(UUID id);
}
