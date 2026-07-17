package com.cypr.modules.billing.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.billing.dto.InvoiceRequestDTO;
import com.cypr.modules.billing.dto.InvoiceResponseDTO;
import com.cypr.modules.billing.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<InvoiceResponseDTO>> createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        return ResponseEntity.ok(BaseResponse.success("Invoice created", invoiceService.createInvoice(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<Page<InvoiceResponseDTO>>> getUserInvoices(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("User invoices fetched", invoiceService.getUserInvoices(userId, pageable)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<InvoiceResponseDTO>> updateInvoiceStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(BaseResponse.success("Invoice status updated", invoiceService.updateInvoiceStatus(id, status)));
    }
}
