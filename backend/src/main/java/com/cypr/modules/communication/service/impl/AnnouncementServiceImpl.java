package com.cypr.modules.communication.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.AnnouncementRequestDTO;
import com.cypr.modules.communication.dto.AnnouncementResponseDTO;
import com.cypr.modules.communication.entity.Announcement;
import com.cypr.modules.communication.mapper.AnnouncementMapper;
import com.cypr.modules.communication.repository.AnnouncementRepository;
import com.cypr.modules.communication.service.AnnouncementService;
import com.cypr.security.HtmlSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final AnnouncementMapper mapper;

    public AnnouncementServiceImpl(AnnouncementRepository repository, AnnouncementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponseDTO getById(UUID id) {
        Announcement entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Announcement not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public AnnouncementResponseDTO create(AnnouncementRequestDTO requestDTO) {
        Announcement entity = mapper.toEntity(requestDTO);
        entity.setTitle(HtmlSanitizer.sanitize(entity.getTitle()));
        entity.setContent(HtmlSanitizer.sanitize(entity.getContent()));
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public AnnouncementResponseDTO update(UUID id, AnnouncementRequestDTO requestDTO) {
        Announcement entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Announcement not found with id: " + id));
        entity.setTitle(HtmlSanitizer.sanitize(requestDTO.getTitle()));
        entity.setContent(HtmlSanitizer.sanitize(requestDTO.getContent()));
        entity.setStartsAt(requestDTO.getStartsAt());
        entity.setEndsAt(requestDTO.getEndsAt());
        entity.setActive(requestDTO.isActive());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        Announcement entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Announcement not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
