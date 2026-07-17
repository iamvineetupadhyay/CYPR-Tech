package com.cypr.modules.system.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.system.dto.SettingRequestDTO;
import com.cypr.modules.system.dto.SettingResponseDTO;
import com.cypr.modules.system.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/system/settings")
@Tag(name = "Setting API", description = "Endpoints for managing Settings")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')") // Securing entire controller
public class SettingController {

    private final SettingService service;

    public SettingController(SettingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Settings", description = "Retrieve a paginated list of Settings")
    public ResponseEntity<BaseResponse<Page<SettingResponseDTO>>> getAll(Pageable pageable) {
        Page<SettingResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Setting by ID")
    public ResponseEntity<BaseResponse<SettingResponseDTO>> getById(@PathVariable UUID id) {
        SettingResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Setting")
    public ResponseEntity<BaseResponse<SettingResponseDTO>> create(@Valid @RequestBody SettingRequestDTO requestDTO) {
        SettingResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Setting")
    public ResponseEntity<BaseResponse<SettingResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody SettingRequestDTO requestDTO) {
        SettingResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Setting")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
