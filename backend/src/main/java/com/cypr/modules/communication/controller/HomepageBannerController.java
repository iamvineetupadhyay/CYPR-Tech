package com.cypr.modules.communication.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.communication.dto.HomepageBannerRequestDTO;
import com.cypr.modules.communication.dto.HomepageBannerResponseDTO;
import com.cypr.modules.communication.service.HomepageBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communication/homepage-banners")
@Tag(name = "HomepageBanner API", description = "Endpoints for managing HomepageBanners")
public class HomepageBannerController {

    private final HomepageBannerService service;

    public HomepageBannerController(HomepageBannerService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all HomepageBanners", description = "Retrieve a paginated list of HomepageBanners")
    public ResponseEntity<BaseResponse<Page<HomepageBannerResponseDTO>>> getAll(Pageable pageable) {
        Page<HomepageBannerResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get HomepageBanner by ID")
    public ResponseEntity<BaseResponse<HomepageBannerResponseDTO>> getById(@PathVariable UUID id) {
        HomepageBannerResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new HomepageBanner")
    public ResponseEntity<BaseResponse<HomepageBannerResponseDTO>> create(@Valid @RequestBody HomepageBannerRequestDTO requestDTO) {
        HomepageBannerResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update HomepageBanner")
    public ResponseEntity<BaseResponse<HomepageBannerResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody HomepageBannerRequestDTO requestDTO) {
        HomepageBannerResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete HomepageBanner")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
