package com.cypr.modules.security.controller;

import com.cypr.common.BaseResponse;
import com.cypr.modules.security.dto.DeviceIPRequestDTO;
import com.cypr.modules.security.dto.DeviceIPResponseDTO;
import com.cypr.modules.security.service.DeviceIPService;
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
@RequestMapping("/api/v1/security/device-ips")
@Tag(name = "DeviceIP API", description = "Endpoints for managing DeviceIPs")
public class DeviceIPController {

    private final DeviceIPService service;

    public DeviceIPController(DeviceIPService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Get all DeviceIPs", description = "Retrieve a paginated list of DeviceIPs")
    public ResponseEntity<BaseResponse<Page<DeviceIPResponseDTO>>> getAll(Pageable pageable) {
        Page<DeviceIPResponseDTO> page = service.getAll(pageable);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get DeviceIP by ID")
    public ResponseEntity<BaseResponse<DeviceIPResponseDTO>> getById(@PathVariable UUID id) {
        DeviceIPResponseDTO responseDTO = service.getById(id);
        return ResponseEntity.ok(BaseResponse.success("Retrieved successfully", responseDTO));
    }

    @PostMapping
    @Operation(summary = "Create new DeviceIP")
    public ResponseEntity<BaseResponse<DeviceIPResponseDTO>> create(@Valid @RequestBody DeviceIPRequestDTO requestDTO) {
        DeviceIPResponseDTO responseDTO = service.create(requestDTO);
        return new ResponseEntity<>(BaseResponse.success("Created successfully", responseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update DeviceIP")
    public ResponseEntity<BaseResponse<DeviceIPResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody DeviceIPRequestDTO requestDTO) {
        DeviceIPResponseDTO responseDTO = service.update(id, requestDTO);
        return ResponseEntity.ok(BaseResponse.success("Updated successfully", responseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete DeviceIP")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted successfully", null));
    }
}
