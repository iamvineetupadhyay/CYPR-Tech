package com.cypr.modules.billing.service;

import com.cypr.modules.billing.dto.CouponRequestDTO;
import com.cypr.modules.billing.dto.CouponResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CouponService {
    Page<CouponResponseDTO> getAll(Pageable pageable);
    CouponResponseDTO getById(UUID id);
    CouponResponseDTO create(CouponRequestDTO requestDTO);
    CouponResponseDTO update(UUID id, CouponRequestDTO requestDTO);
    void delete(UUID id);
}
