package com.cypr.modules.billing.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.billing.dto.RefundRequestDTO;
import com.cypr.modules.billing.dto.RefundResponseDTO;
import com.cypr.modules.billing.entity.Payment;
import com.cypr.modules.billing.entity.Refund;
import com.cypr.modules.billing.repository.PaymentRepository;
import com.cypr.modules.billing.repository.RefundRepository;
import com.cypr.modules.billing.service.RefundService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;

    public RefundServiceImpl(RefundRepository refundRepository, PaymentRepository paymentRepository) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public RefundResponseDTO processRefund(RefundRequestDTO request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new BusinessException("Payment not found"));
                
        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(request.getStatus());
        
        refund = refundRepository.save(refund);
        
        // Mock actual gateway call here
        if ("COMPLETED".equals(request.getStatus())) {
            // we could update payment status to "REFUNDED" or "PARTIALLY_REFUNDED"
            // For now, let's keep payment as is and log refund
        }

        return mapToDTO(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundResponseDTO> getPaymentRefunds(UUID paymentId, Pageable pageable) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment not found"));
        return refundRepository.findByPayment(payment, pageable).map(this::mapToDTO);
    }

    private RefundResponseDTO mapToDTO(Refund refund) {
        RefundResponseDTO dto = new RefundResponseDTO();
        dto.setId(refund.getId());
        dto.setPaymentId(refund.getPayment().getId());
        dto.setAmount(refund.getAmount());
        dto.setReason(refund.getReason());
        dto.setStatus(refund.getStatus());
        return dto;
    }
}
