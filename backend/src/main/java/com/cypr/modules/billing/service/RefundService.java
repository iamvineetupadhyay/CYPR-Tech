package com.cypr.modules.billing.service;

import com.cypr.modules.billing.dto.RefundRequestDTO;
import com.cypr.modules.billing.dto.RefundResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface RefundService {
    RefundResponseDTO processRefund(RefundRequestDTO request);
    Page<RefundResponseDTO> getPaymentRefunds(UUID paymentId, Pageable pageable);
}
