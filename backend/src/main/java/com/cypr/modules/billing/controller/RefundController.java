package com.cypr.modules.billing.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.billing.dto.RefundRequestDTO;
import com.cypr.modules.billing.dto.RefundResponseDTO;
import com.cypr.modules.billing.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<RefundResponseDTO>> processRefund(@Valid @RequestBody RefundRequestDTO request) {
        return ResponseEntity.ok(BaseResponse.success("Refund processed", refundService.processRefund(request)));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<BaseResponse<Page<RefundResponseDTO>>> getPaymentRefunds(@PathVariable UUID paymentId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Payment refunds fetched", refundService.getPaymentRefunds(paymentId, pageable)));
    }
}
