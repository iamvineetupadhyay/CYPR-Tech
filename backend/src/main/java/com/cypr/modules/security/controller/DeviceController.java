package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.DeviceRequestDTO;
import com.cypr.modules.security.dto.DeviceResponseDTO;
import com.cypr.modules.security.service.DeviceService;
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
@RequestMapping("/api/v1/security/devices")
@Tag(name = "Device API", description = "Endpoints for managing Devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all Devices", description = "Retrieve a paginated list of Devices")
    public ResponseEntity<BaseResponse<Page<DeviceResponseDTO>>> getAll(Pageable pageable) {
        Page<DeviceResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Device by ID")
    public ResponseEntity<BaseResponse<DeviceResponseDTO>> getById(@PathVariable UUID id) {
        DeviceResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new Device")
    public ResponseEntity<BaseResponse<DeviceResponseDTO>> create(@Valid @RequestBody DeviceRequestDTO requestDTO) {
        DeviceResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Device")
    public ResponseEntity<BaseResponse<DeviceResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody DeviceRequestDTO requestDTO) {
        DeviceResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Device")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
