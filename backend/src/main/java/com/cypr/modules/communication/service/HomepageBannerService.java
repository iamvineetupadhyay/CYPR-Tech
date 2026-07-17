package com.cypr.modules.communication.service;

import com.cypr.modules.communication.dto.HomepageBannerRequestDTO;
import com.cypr.modules.communication.dto.HomepageBannerResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface HomepageBannerService {
    Page<HomepageBannerResponseDTO> getAll(Pageable pageable);
    HomepageBannerResponseDTO getById(UUID id);
    HomepageBannerResponseDTO create(HomepageBannerRequestDTO requestDTO);
    HomepageBannerResponseDTO update(UUID id, HomepageBannerRequestDTO requestDTO);
    void delete(UUID id);
}
