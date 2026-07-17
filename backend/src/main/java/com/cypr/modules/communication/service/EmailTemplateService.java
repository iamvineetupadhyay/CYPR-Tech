package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.EmailTemplateRequestDTO;
import com.cypr.modules.communication.dto.EmailTemplateResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface EmailTemplateService {
    Page<EmailTemplateResponseDTO> getAll(Pageable pageable);
    EmailTemplateResponseDTO getById(UUID id);
    EmailTemplateResponseDTO create(EmailTemplateRequestDTO requestDTO);
    EmailTemplateResponseDTO update(UUID id, EmailTemplateRequestDTO requestDTO);
    void delete(UUID id);
}
