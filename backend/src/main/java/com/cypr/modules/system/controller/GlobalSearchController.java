package com.cypr.modules.system.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.system.dto.GlobalSearchDTO;
import com.cypr.modules.system.service.GlobalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Global Search API", description = "Endpoints for Global Search across all modules")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    public GlobalSearchController(GlobalSearchService globalSearchService) {
        this.globalSearchService = globalSearchService;
    }

    @GetMapping
    @Operation(summary = "Global Search", description = "Search across Users, Tickets, Payments, Logs, Settings, and Announcements")
    public ResponseEntity<BaseResponse<Page<GlobalSearchDTO>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<GlobalSearchDTO> results = globalSearchService.searchGlobal(q, pageable);
        
        return ResponseEntity.ok(BaseResponse.success("Search completed", results));
    }
}
