package com.cypr.modules.billing.service.impl;

import com.cypr.entity.User;
import com.cypr.exception.BusinessException;
import com.cypr.modules.billing.dto.InvoiceRequestDTO;
import com.cypr.modules.billing.dto.InvoiceResponseDTO;
import com.cypr.modules.billing.entity.Invoice;
import com.cypr.modules.billing.entity.Subscription;
import com.cypr.modules.billing.repository.InvoiceRepository;
import com.cypr.modules.billing.repository.SubscriptionRepository;
import com.cypr.modules.billing.service.InvoiceService;
import com.cypr.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));
        
        Invoice invoice = new Invoice();
        invoice.setUser(user);
        
        if (request.getSubscriptionId() != null) {
            Subscription sub = subscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new BusinessException("Subscription not found"));
            invoice.setSubscription(sub);
        }
        
        invoice.setAmount(request.getAmount());
        invoice.setCurrency(request.getCurrency());
        invoice.setStatus(request.getStatus());
        invoice.setDueDate(request.getDueDate());
        invoice.setPdfUrl(request.getPdfUrl());
        
        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponseDTO> getUserInvoices(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        return invoiceRepository.findByUser(user, pageable).map(this::mapToDTO);
    }

    @Override
    public InvoiceResponseDTO updateInvoiceStatus(UUID invoiceId, String status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new BusinessException("Invoice not found"));
        invoice.setStatus(status);
        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    private InvoiceResponseDTO mapToDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setUserId(invoice.getUser().getId());
        dto.setSubscriptionId(invoice.getSubscription() != null ? invoice.getSubscription().getId() : null);
        dto.setAmount(invoice.getAmount());
        dto.setCurrency(invoice.getCurrency());
        dto.setStatus(invoice.getStatus());
        dto.setDueDate(invoice.getDueDate());
        dto.setPdfUrl(invoice.getPdfUrl());
        return dto;
    }
}
