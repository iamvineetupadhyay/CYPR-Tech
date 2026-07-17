package com.cypr.modules.communication.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.communication.dto.HomepageBannerRequestDTO;
import com.cypr.modules.communication.dto.HomepageBannerResponseDTO;
import com.cypr.modules.communication.entity.HomepageBanner;
import com.cypr.modules.communication.mapper.HomepageBannerMapper;
import com.cypr.modules.communication.repository.HomepageBannerRepository;
import com.cypr.modules.communication.service.HomepageBannerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class HomepageBannerServiceImpl implements HomepageBannerService {

    private final HomepageBannerRepository repository;
    private final HomepageBannerMapper mapper;

    public HomepageBannerServiceImpl(HomepageBannerRepository repository, HomepageBannerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomepageBannerResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public HomepageBannerResponseDTO getById(UUID id) {
        HomepageBanner entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("HomepageBanner not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public HomepageBannerResponseDTO create(HomepageBannerRequestDTO requestDTO) {
        HomepageBanner entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public HomepageBannerResponseDTO update(UUID id, HomepageBannerRequestDTO requestDTO) {
        HomepageBanner entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("HomepageBanner not found with id: " + id));
        entity.setImageUrl(requestDTO.getImageUrl());
        entity.setLinkUrl(requestDTO.getLinkUrl());
        entity.setActive(requestDTO.isActive());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        HomepageBanner entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("HomepageBanner not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
