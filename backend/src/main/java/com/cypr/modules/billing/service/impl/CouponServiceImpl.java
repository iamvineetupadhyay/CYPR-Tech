package com.cypr.modules.billing.service.impl;

import com.cypr.exception.BusinessException;
import com.cypr.modules.billing.dto.CouponRequestDTO;
import com.cypr.modules.billing.dto.CouponResponseDTO;
import com.cypr.modules.billing.entity.Coupon;
import com.cypr.modules.billing.mapper.CouponMapper;
import com.cypr.modules.billing.repository.CouponRepository;
import com.cypr.modules.billing.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;
    private final CouponMapper mapper;

    public CouponServiceImpl(CouponRepository repository, CouponMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDTO getById(UUID id) {
        Coupon entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Coupon not found with id: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    public CouponResponseDTO create(CouponRequestDTO requestDTO) {
        Coupon entity = mapper.toEntity(requestDTO);
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public CouponResponseDTO update(UUID id, CouponRequestDTO requestDTO) {
        Coupon entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Coupon not found with id: " + id));
        entity.setCode(requestDTO.getCode());
        entity.setDiscountPercentage(requestDTO.getDiscountPercentage());
        entity.setMaxUses(requestDTO.getMaxUses());
        entity.setUsesCount(requestDTO.getUsesCount());
        entity.setExpiresAt(requestDTO.getExpiresAt());
        entity = repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public void delete(UUID id) {
        Coupon entity = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Coupon not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }
}
