package com.cypr.modules.billing.service;

import com.cypr.modules.billing.dto.InvoiceRequestDTO;
import com.cypr.modules.billing.dto.InvoiceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface InvoiceService {
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO request);
    Page<InvoiceResponseDTO> getUserInvoices(Long userId, Pageable pageable);
    InvoiceResponseDTO updateInvoiceStatus(UUID invoiceId, String status);
}
