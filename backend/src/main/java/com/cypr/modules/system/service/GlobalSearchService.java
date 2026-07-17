package com.cypr.modules.system.service;

import com.cypr.modules.system.dto.GlobalSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GlobalSearchService {
    Page<GlobalSearchDTO> searchGlobal(String query, Pageable pageable);
}
