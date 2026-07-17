package com.cypr.modules.billing.service;

import com.cypr.modules.billing.dto.PaymentRequestDTO;
import com.cypr.modules.billing.dto.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PaymentService {
    Page<PaymentResponseDTO> getAll(Pageable pageable);
    PaymentResponseDTO getById(UUID id);
    PaymentResponseDTO create(PaymentRequestDTO requestDTO);
    PaymentResponseDTO update(UUID id, PaymentRequestDTO requestDTO);
    void delete(UUID id);
}
