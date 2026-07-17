package com.cypr.modules.communication.mapper;

import com.cypr.modules.communication.dto.EmailTemplateRequestDTO;
import com.cypr.modules.communication.dto.EmailTemplateResponseDTO;
import com.cypr.modules.communication.entity.EmailTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateMapper {

    public EmailTemplate toEntity(EmailTemplateRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        EmailTemplate entity = new EmailTemplate();
        entity.setTemplateCode(requestDTO.getTemplateCode());
        entity.setSubject(requestDTO.getSubject());
        entity.setBodyHtml(requestDTO.getBodyHtml());
        entity.setVariables(requestDTO.getVariables());
        return entity;
    }

    public EmailTemplateResponseDTO toResponseDTO(EmailTemplate entity) {
        if (entity == null) return null;
        EmailTemplateResponseDTO dto = new EmailTemplateResponseDTO();
        dto.setId(entity.getId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setSubject(entity.getSubject());
        dto.setBodyHtml(entity.getBodyHtml());
        dto.setVariables(entity.getVariables());
        return dto;
    }
}
