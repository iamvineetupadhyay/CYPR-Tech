package com.cypr.modules.communication.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.EmailTemplateRequestDTO;
import com.cypr.modules.communication.dto.EmailTemplateResponseDTO;
import com.cypr.modules.communication.entity.EmailTemplate;
import com.cypr.modules.communication.mapper.EmailTemplateMapper;
import com.cypr.modules.communication.repository.EmailTemplateRepository;
import com.cypr.modules.communication.service.EmailTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository repository;
    private final EmailTemplateMapper mapper;

    public EmailTemplateServiceImpl(EmailTemplateRepository repository, EmailTemplateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmailTemplateResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EmailTemplateResponseDTO getById(UUID id) {
        EmailTemplate entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("EmailTemplate not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public EmailTemplateResponseDTO create(EmailTemplateRequestDTO requestDTO) {
        EmailTemplate entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public EmailTemplateResponseDTO update(UUID id, EmailTemplateRequestDTO requestDTO) {
        EmailTemplate entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("EmailTemplate not found with id: " + id));
        entity.setTemplateCode(requestDTO.getTemplateCode());
        entity.setSubject(requestDTO.getSubject());
        entity.setBodyHtml(requestDTO.getBodyHtml());
        entity.setVariables(requestDTO.getVariables());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        EmailTemplate entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("EmailTemplate not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
